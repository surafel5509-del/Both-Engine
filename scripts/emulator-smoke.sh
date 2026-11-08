#!/usr/bin/env bash
#
# Both Engine - emulator smoke test
#
# Installs every built APK (debug and release) on the running emulator, launches
# the app and asserts that it is genuinely usable:
#
#   * the process is still alive
#   * the activity was reported as displayed (a window actually appeared)
#   * the GL surface was created and frames were rendered
#   * no crash trace, native fault, ANR or render-loop error occurred
#
# Failures write the crash trace to $GITHUB_STEP_SUMMARY and emit annotations so
# the reason can be read from the checks API.
#
# Exit codes: 0 = all APKs passed, 1 = an APK failed, 2 = harness problem.

set -uo pipefail

PROJECT_DIR="${GITHUB_WORKSPACE:-$(cd "$(dirname "$0")/.." && pwd)}"
APK_ROOT="$PROJECT_DIR/app/build/outputs/apk"
OUT_DIR="${SMOKE_OUT_DIR:-$(pwd)}"
ENV_FILE="$OUT_DIR/smoke.env"
SUMMARY="${GITHUB_STEP_SUMMARY:-/dev/null}"
PKG="com.bothengine.game"
ACTIVITY="$PKG/com.bothengine.GameActivity"

mkdir -p "$OUT_DIR"
: > "$ENV_FILE"

annotate() { echo "::$1 title=$2::$3"; }
note_env() { echo "$1" >> "$ENV_FILE"; }
summary() { echo "$1" >> "$SUMMARY"; }

# ------------------------------------------------------------------ APK list
if [ -z "$(find "$APK_ROOT" -name '*.apk' 2>/dev/null | head -n1)" ]; then
  note_env "No APK under $APK_ROOT; building"
  ( cd "$PROJECT_DIR" && ./gradlew --no-daemon assembleDebug assembleRelease ) >> "$ENV_FILE" 2>&1 || true
fi

APKS=()
while IFS= read -r candidate; do
  case "$candidate" in
    *unsigned*) continue ;;
  esac
  APKS+=("$candidate")
done < <(find "$APK_ROOT" -name '*.apk' 2>/dev/null | sort)

if [ ${#APKS[@]} -eq 0 ]; then
  note_env "failure: no APK produced"
  summary "### Harness error"
  summary "No APK was produced under \`$APK_ROOT\`."
  exit 2
fi

FAILED=0

# -------------------------------------------------------------- test one APK
test_apk() {
  local apk="$1" label="$2" logcat="$OUT_DIR/logcat-$2.txt" amfile="$OUT_DIR/am-start-$2.txt"

  summary ""
  summary "### ${label} APK"
  summary "\`$(basename "$apk")\`"

  adb uninstall "$PKG" >/dev/null 2>&1 || true

  local install_out
  install_out="$(adb install -r -t "$apk" 2>&1)"
  note_env "INSTALL[$label]=$install_out"
  if ! printf '%s' "$install_out" | grep -qi "Success"; then
    annotate error "Install-$label" "$(printf '%s' "$install_out" | tr '\n' ' ')"
    summary "Install failed: $(printf '%s' "$install_out" | tr '\n' ' ')"
    return 1
  fi

  # A locked/blank device never creates a surface, so wake and unlock first.
  adb shell input keyevent KEYCODE_WAKEUP >/dev/null 2>&1 || true
  adb shell wm dismiss-keyguard >/dev/null 2>&1 || true

  adb logcat -c 2>/dev/null || true
  adb logcat -v threadtime > "$logcat" 2>&1 &
  local log_pid=$!
  sleep 2

  adb shell am start -W -n "$ACTIVITY" > "$amfile" 2>&1 || true
  if grep -q "Error" "$amfile"; then
    adb shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1 >> "$amfile" 2>&1 || true
  fi

  sleep 15

  local pid
  pid="$(adb shell pidof "$PKG" 2>/dev/null | tr -d '\r')"
  note_env "PID[$label]=$pid"

  kill "$log_pid" 2>/dev/null || true
  wait "$log_pid" 2>/dev/null || true

  adb shell dumpsys gfxinfo "$PKG" > "$OUT_DIR/gfxinfo-$label.txt" 2>/dev/null || true
  local frames
  frames="$(grep -oE "Total frames rendered: [0-9]+" "$OUT_DIR/gfxinfo-$label.txt" 2>/dev/null |
    head -n1 | grep -oE "[0-9]+$")"
  case "$frames" in ''|*[!0-9]*) frames=0 ;; esac
  note_env "FRAMES[$label]=$frames"

  # ---- crash trace
  local crash
  crash="$(sed -n '/FATAL EXCEPTION/,/^[0-9]/p' "$logcat" 2>/dev/null | head -n 60)"
  if [ -n "$crash" ]; then
    summary '```'
    summary "$crash"
    summary '```'
    echo "$crash" | head -n 18 | while IFS= read -r line; do annotate error "Crash-$label" "$line"; done
    return 1
  fi

  if grep -qE "Fatal signal|ANR in $PKG" "$logcat" 2>/dev/null; then
    grep -E "Fatal signal|ANR in $PKG|backtrace" "$logcat" | head -n 10 |
      while IFS= read -r line; do annotate error "Native-crash-$label" "$line"; done
    return 1
  fi

  # ---- render loop errors
  local render_errors
  render_errors="$(grep -E "Error in (onDrawFrame|onSurfaceCreated|onSurfaceChanged)|Failed to initialize OpenGL" "$logcat" 2>/dev/null | head -n 6)"
  if [ -n "$render_errors" ]; then
    echo "$render_errors" | while IFS= read -r line; do annotate error "Render-error-$label" "$line"; done
    summary "Render loop reported errors (see annotations)."
    return 1
  fi

  if [ -z "$pid" ]; then
    grep -E "com\.bothengine|AndroidRuntime" "$logcat" 2>/dev/null | tail -n 10 |
      while IFS= read -r line; do annotate error "App-stopped-$label" "$line"; done
    sed -n '1,6p' "$amfile" 2>/dev/null |
      while IFS= read -r line; do annotate error "am-start-$label" "$line"; done
    summary "Process was not running 15s after launch."
    return 1
  fi

  # ---- positive proof: the screen must actually contain a rendered image.
  # "Process alive" and "frames rendered" both stayed green on a purely black
  # screen, so the framebuffer is sampled and checked for non-black pixels.
  local shot="$OUT_DIR/screen-$label.png"
  adb exec-out screencap -p > "$shot" 2>/dev/null || true
  local colours=""
  if [ -s "$shot" ]; then
    colours="$(python3 "$PROJECT_DIR/scripts/screenshot_colours.py" "$shot" 2>/dev/null || true)"
  fi
  note_env "COLOURS[$label]=$colours"

  # Publish the frame as base64 inside a collapsible summary block: CI artefacts
  # are not retrievable for this job, and a picture settles "is it really
  # drawing?" faster than any number.
  if [ -s "$shot" ] && command -v base64 >/dev/null 2>&1; then
    summary "<details><summary>Screenshot ($label)</summary>"
    summary ""
    summary "\`\`\`"
    summary "base64:$(base64 -w0 "$shot" 2>/dev/null | head -c 400000)"
    summary "\`\`\`"
    summary "</details>"
  fi
  local distinct bright
  distinct="$(printf '%s' "$colours" | sed -n 's/^distinct=//p')"
  warm="$(printf '%s' "$colours" | sed -n 's/^warm=//p')"
  case "$warm" in ''|*[!0-9]*) warm=0 ;; esac
  bright="$(printf '%s' "$colours" | sed -n 's/^bright=//p')"
  case "$distinct" in ''|*[!0-9]*) distinct=0 ;; esac
  case "$bright" in ''|*[!0-9]*) bright=0 ;; esac

  summary "Screen: $distinct distinct colours, $bright% non-black, **$warm% warm (scene content)**."
  printf '%s\n' "$colours" | sed -n '/^preview:/,$p' | sed 's/^/    /' >> "$SUMMARY" || true

  # The job log lives on a host that is not always reachable, so publish a
  # compact view of the actual framebuffer as an annotation too.
  local preview
  preview="$(printf '%s\n' "$colours" | sed -n '/^preview:/,$p' | sed '1d' | tr -d '\n' | tr -d '|')"
  if [ -n "$preview" ]; then
    annotate notice "Framebuffer-$label" "$preview"
  fi

  if [ "$distinct" -lt 8 ] || [ "$bright" -lt 3 ]; then
    annotate error "Black-screen-$label" \
      "Screen is effectively blank ($distinct colours, $bright% non-black pixels)"
    summary "FAIL: the screen is blank."
    return 1
  fi

  # A cleared buffer plus a gradient background would pass the test above even
  # with no geometry drawn, so require the lit cube (warm pixels) to be present.
  if [ "$warm" -lt 2 ]; then
    annotate error "No-geometry-$label" \
      "Background is drawn but no scene geometry is visible ($warm% warm pixels)"
    summary "FAIL: nothing but a background is drawn."
    return 1
  fi

  local displayed surface
  displayed="$(grep -m1 "Displayed $PKG" "$logcat" 2>/dev/null | tr -d '\r')"
  surface="$(grep -m1 "Surface created" "$logcat" 2>/dev/null | tr -d '\r')"

  if [ -z "$displayed" ]; then
    annotate error "Not-displayed-$label" "Activity never reported as displayed; no visible window"
    return 1
  fi
  if [ -z "$surface" ]; then
    annotate error "No-surface-$label" "GL surface was never created"
    return 1
  fi
  # gfxinfo under-reports for GLSurfaceView, so also count the renderer's own
  # frame log and use whichever is larger.
  local log_frames
  log_frames="$(grep -oE "Rendering frame [0-9]+" "$logcat" 2>/dev/null |
    grep -oE "[0-9]+$" | sort -n | tail -n1)"
  case "$log_frames" in ''|*[!0-9]*) log_frames=0 ;; esac
  [ "$log_frames" -gt "$frames" ] && frames="$log_frames"
  note_env "FRAMES[$label]=$frames"

  if [ "$frames" -lt 30 ]; then
    annotate error "Render-stalled-$label" \
      "Only $frames frames in ~15s; the render loop is not running continuously"
    return 1
  fi

  summary "App log:"
  summary '```'
  grep -E "(GameActivity|EngineApplication|RenderManager|Renderer|FallbackRenderer):|Displayed $PKG" \
    "$logcat" 2>/dev/null | tail -n 10 | sed 's/^/    /' >> "$SUMMARY"
  summary '```'
  summary ""

  annotate notice "Smoke test $label" \
    "PASS: displayed, GL surface created, $frames frames, $distinct colours, $warm% scene pixels, pid $pid alive"
  summary "PASS: displayed, GL surface created, **$frames frames** rendered, " \
    "$distinct colours on screen, pid \`$pid\` alive."
  return 0
}

for apk in "${APKS[@]}"; do
  case "$apk" in
    *release*) label="release" ;;
    *)         label="debug" ;;
  esac
  if ! test_apk "$apk" "$label"; then
    FAILED=1
  fi
done

exit "$FAILED"
