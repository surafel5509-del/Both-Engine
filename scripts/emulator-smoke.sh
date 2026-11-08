#!/usr/bin/env bash
#
# Both Engine - emulator smoke test
#
# Installs the debug APK on the running emulator, launches the app and asserts
# that the process is still alive. Any crash trace is written to
# $GITHUB_STEP_SUMMARY and emitted as annotations so it can be read from the
# checks API.
#
# Exit codes: 0 = app launched and stayed alive, 1 = crash / not running,
#             2 = harness problem (missing APK, install failure).

set -uo pipefail

PROJECT_DIR="${GITHUB_WORKSPACE:-$(cd "$(dirname "$0")/.." && pwd)}"
APK_DIR="$PROJECT_DIR/app/build/outputs/apk/debug"
OUT_DIR="${SMOKE_OUT_DIR:-$(pwd)}"
ENV_FILE="$OUT_DIR/smoke.env"
LOGCAT_FILE="$OUT_DIR/logcat.txt"
AM_FILE="$OUT_DIR/am-start.txt"
PKG="com.bothengine.game"
ACTIVITY="$PKG/com.bothengine.GameActivity"

mkdir -p "$OUT_DIR"
: > "$ENV_FILE"

annotate() { echo "::$1 title=$2::$3"; }

note_env() { echo "$1" >> "$ENV_FILE"; }

# ---------------------------------------------------------------- APK resolve
APK="$(find "$APK_DIR" -name '*.apk' 2>/dev/null | head -n1)"
if [ -z "$APK" ]; then
  note_env "No APK in $APK_DIR; building"
  ( cd "$PROJECT_DIR" && ./gradlew --no-daemon assembleDebug ) >> "$ENV_FILE" 2>&1 || true
  APK="$(find "$APK_DIR" -name '*.apk' 2>/dev/null | head -n1)"
fi
note_env "APK=$APK"
note_env "PWD=$(pwd)"

if [ -z "$APK" ] || [ ! -f "$APK" ]; then
  note_env "INSTALL_BEGIN"
  note_env "failure: no APK produced"
  note_env "INSTALL_END"
  echo "### Harness error" >> "${GITHUB_STEP_SUMMARY:-/dev/null}"
  echo "No APK was produced at \`$APK_DIR\`." >> "${GITHUB_STEP_SUMMARY:-/dev/null}"
  exit 2
fi

# ---------------------------------------------------------------- install
note_env "INSTALL_BEGIN"
adb install -r -t "$APK" >> "$ENV_FILE" 2>&1
INSTALL_RC=$?
note_env "INSTALL_END"
if [ $INSTALL_RC -ne 0 ]; then
  echo "### Install failed" >> "${GITHUB_STEP_SUMMARY:-/dev/null}"
  sed -n '/INSTALL_BEGIN/,/INSTALL_END/p' "$ENV_FILE" >> "${GITHUB_STEP_SUMMARY:-/dev/null}"
  exit 2
fi

adb shell pm list packages 2>/dev/null | grep -q "$PKG" || {
  note_env "failure: package $PKG missing after install"
  exit 2
}

# ---------------------------------------------------------------- launch
# A locked/blank device never creates a surface, so wake and unlock first.
adb shell input keyevent KEYCODE_WAKEUP >/dev/null 2>&1 || true
adb shell wm dismiss-keyguard >/dev/null 2>&1 || true

adb logcat -c 2>/dev/null || true
adb logcat -v threadtime > "$LOGCAT_FILE" 2>&1 &
LOGCAT_PID=$!
sleep 2

adb shell am start -W -n "$ACTIVITY" > "$AM_FILE" 2>&1 || true
if grep -q "Error" "$AM_FILE"; then
  # Fall back to the manifest's launcher intent.
  adb shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1 >> "$AM_FILE" 2>&1 || true
fi
head -n 6 "$AM_FILE" >> "$ENV_FILE"

sleep 15

PID="$(adb shell pidof "$PKG" 2>/dev/null | tr -d '\r')"
note_env "PID=$PID"

kill "$LOGCAT_PID" 2>/dev/null || true
wait "$LOGCAT_PID" 2>/dev/null || true

adb shell dumpsys activity activities > "$OUT_DIR/activities.txt" 2>/dev/null || true
adb shell dumpsys gfxinfo "$PKG" > "$OUT_DIR/gfxinfo.txt" 2>/dev/null || true
FRAMES="$(grep -E "Total frames rendered" "$OUT_DIR/gfxinfo.txt" 2>/dev/null | head -n1 | tr -d '\r' | sed 's/.*rendered:[[:space:]]*//')"
case "$FRAMES" in ''|*[!0-9]*) FRAMES=0 ;; esac
note_env "FRAMES=$FRAMES"
note_env "FOCUS=$(adb shell dumpsys window 2>/dev/null | grep -m1 mCurrentFocus | tr -d '\r')"

# ---------------------------------------------------------------- verdict
CRASH="$(sed -n '/FATAL EXCEPTION/,/^$/p' "$LOGCAT_FILE" 2>/dev/null | head -n 60)"
if [ -n "$CRASH" ]; then
  {
    echo "### Crash trace"
    echo '```'
    echo "$CRASH"
    echo '```'
  } >> "${GITHUB_STEP_SUMMARY:-/dev/null}"
  echo "$CRASH" | head -n 18 | while IFS= read -r line; do annotate error Crash "$line"; done
  exit 1
fi

if grep -qE "Fatal signal|ANR in $PKG" "$LOGCAT_FILE" 2>/dev/null; then
  grep -E "Fatal signal|ANR in $PKG|backtrace" "$LOGCAT_FILE" | head -n 10 |
    while IFS= read -r line; do annotate error Native-crash "$line"; done
  exit 1
fi

if [ -z "$PID" ]; then
  grep -E "com\.bothengine|AndroidRuntime" "$LOGCAT_FILE" 2>/dev/null | head -n 12 |
    while IFS= read -r line; do annotate error App-stopped "$line"; done
  sed -n '1,8p' "$AM_FILE" 2>/dev/null |
    while IFS= read -r line; do annotate error "am-start" "$line"; done
  exit 1
fi

# Healthy: report proof so the run page shows the app really started.
# Match our own log tags and the activity record: a bare "Engine" substring
# collides with unrelated system apps (e.g. BugleRcsEngine).
APP_LOG="$(grep -E "(GameActivity|EngineApplication|RenderManager|Renderer|FallbackRenderer):|Displayed $PKG" \
  "$LOGCAT_FILE" 2>/dev/null | tail -n 12)"
if [ -n "$APP_LOG" ]; then
  echo "$APP_LOG" | while IFS= read -r line; do annotate notice "App-log" "$line"; done
else
  grep -F "$PKG" "$LOGCAT_FILE" 2>/dev/null | tail -n 8 |
    while IFS= read -r line; do annotate notice "App-log" "$line"; done
fi

FOCUS_LINE="$(grep -m1 "mCurrentFocus" "$OUT_DIR/../smoke.env" 2>/dev/null)"
[ -n "$FOCUS_LINE" ] && annotate notice "Focus" "$FOCUS_LINE"

STATE="$(grep -E "mResumedActivity|topResumedActivity" "$OUT_DIR/activities.txt" 2>/dev/null | head -n 2)"
if [ -n "$STATE" ]; then
  echo "$STATE" | while IFS= read -r line; do annotate notice "Activity-state" "$line"; done
fi

# A stored crash report means the process died earlier and came back.
CRASH_FILE="$(adb shell run-as "$PKG" cat files/last_crash.txt 2>/dev/null | tr -d '\r')"
if [ -n "$CRASH_FILE" ]; then
  echo "$CRASH_FILE" | head -n 20 | while IFS= read -r line; do annotate error "Stored-crash" "$line"; done
  exit 1
fi

RENDER_ERRORS="$(grep -E "Error in (onDrawFrame|onSurfaceCreated|onSurfaceChanged)" "$LOGCAT_FILE" 2>/dev/null | head -n 6)"
if [ -n "$RENDER_ERRORS" ]; then
  echo "$RENDER_ERRORS" | while IFS= read -r line; do annotate error "Render-error" "$line"; done
  exit 1
fi

annotate notice "Frames" "Total frames rendered: $FRAMES"

if [ "$FRAMES" -le 0 ]; then
  annotate error "No-frames" "Process is alive but rendered 0 frames (no surface)"
  exit 1
fi

annotate notice "Smoke test" "App is running (pid $PID) and rendered $FRAMES frames"
exit 0
