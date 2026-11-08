# Building Both Engine APK

## Prerequisites

To build the Both Engine APK, you need:

### 1. Java Development Kit (JDK)
- **Minimum version:** Java 8 (JDK 1.8)
- **Recommended:** Java 11 or Java 17
- **Download:** https://www.oracle.com/java/technologies/javase-downloads.html

### 2. Android SDK
- **Minimum API:** 24 (Android 7.0 Nougat)
- **Target API:** 34 (Android 14)
- **Build Tools:** 34.0.0

### 3. Android Studio (Recommended)
- **Version:** 2023.2 or later
- **Download:** https://developer.android.com/studio

### 4. Environment Variables

#### On Linux/macOS:
```bash
# Add to ~/.bashrc or ~/.zshrc
export JAVA_HOME=/path/to/jdk
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

#### On Windows:
```batch
set JAVA_HOME=C:\Program Files\Java\jdk-17
set ANDROID_HOME=C:\Users\YourName\AppData\Local\Android\Sdk
set PATH=%PATH%;%JAVA_HOME%\bin
set PATH=%PATH%;%ANDROID_HOME%\tools
set PATH=%PATH%;%ANDROID_HOME%\platform-tools
```

## Building the APK with GitHub Actions (recommended)

The repository ships with `.github/workflows/build-apk.yml`, so no local
Android SDK is needed. Every push to `main`, `master` or a work branch builds a
debug APK automatically.

### Get the APK

1. Open the **Actions** tab of the repository.
2. Pick the latest **Build APK** run.
3. Download the **BothEngine-debug-apk** artifact (retained for 30 days).

The run summary also lists the version, commit, size and SHA-256 of the APK.

### Trigger a build manually

*Actions → Build APK → Run workflow*. Two optional inputs are available:

| Input | Effect |
| --- | --- |
| `build_release` | Also builds a release APK |
| `publish_release` | Builds both APKs and attaches them to a GitHub Release |

### Release builds and signing

Pushing a tag such as `v1.2.0` builds and publishes a release. To sign it,
add these repository secrets (Settings → Secrets and variables → Actions):

| Secret | Purpose |
| --- | --- |
| `KEYSTORE_BASE64` | Base64 of your `.keystore` file (`base64 -w0 app.keystore`) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

Without these secrets the release APK is signed with the debug key so it still
installs on a device — rotate to a real key before publishing to the Play Store.

### Install the artifact

```bash
adb install BothEngine-1.0.<build>.apk
```

Android 7.0 (API 24) or newer with OpenGL ES 2.0 is required.

## Building the APK

### Method 1: Using Android Studio (Recommended)

1. **Open the project in Android Studio:**
   - File → Open → Select the Both-Engine directory
   
2. **Wait for Gradle sync:**
   - Android Studio will automatically download dependencies
   
3. **Build the debug APK:**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or click the "Build" button in the toolbar
   
4. **Find the APK:**
   - The debug APK will be created at:
     `app/build/outputs/apk/debug/app-debug.apk`
   
5. **Run on device:**
   - Connect your Android device via USB
   - Enable USB debugging in developer options
   - Click "Run" button in Android Studio

### Method 2: Using Command Line

#### On Linux/macOS:

```bash
# Navigate to project directory
cd Both-Engine

# Make gradlew executable (first time only)
chmod +x gradlew

# Download Gradle (first time only - will take a few minutes)
./gradlew --version

# Build debug APK
./gradlew clean assembleDebug

# Build release APK (requires signing)
./gradlew clean assembleRelease
```

#### On Windows:

```batch
cd Both-Engine

:: Download Gradle (first time only)
gradlew.bat --version

:: Build debug APK
gradlew.bat clean assembleDebug

:: Build release APK (requires signing)
gradlew.bat clean assembleRelease
```

### Method 3: Using Gradle Directly

If you have Gradle installed globally:

```bash
cd Both-Engine
gradle clean assembleDebug
```

## APK Output Locations

### Debug APK
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (Unsigned)
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

### Release APK (Signed)
If you configure signing:
```
app/build/outputs/apk/release/app-release.apk
```

## Signing the APK (For Release)

To distribute your APK, you need to sign it:

### 1. Generate a Keystore

```bash
keytool -genkey -v -keystore both-engine-release.keystore \
    -alias both-engine -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure Signing in app/build.gradle

Add to `app/build.gradle`:

```gradle
android {
    signingConfigs {
        release {
            storeFile file('both-engine-release.keystore')
            storePassword 'yourpassword'
            keyAlias 'both-engine'
            keyPassword 'yourpassword'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### 3. Build Signed APK

```bash
./gradlew clean assembleRelease
```

The signed APK will be at:
```
app/build/outputs/apk/release/app-release.apk
```

## Running the APK

### Install on Device via ADB

```bash
# Connect device
adb devices

# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release APK
adb install app/build/outputs/apk/release/app-release.apk
```

### Run Directly from Android Studio

1. Connect your Android device via USB
2. Enable USB debugging in developer options
3. Click the "Run" button (green triangle) in Android Studio
4. Select your device and click "OK"

## Troubleshooting

### Error: "Failed to install Android SDK"
- Make sure ANDROID_HOME is set correctly
- Install required SDK packages:
  ```bash
  sdkmanager "platforms;android-34"
  sdkmanager "build-tools;34.0.0"
  sdkmanager "platform-tools"
  ```

### Error: "Java not found"
- Install JDK and set JAVA_HOME
- Verify with: `java -version`

### Error: "Gradle not found"
- The gradle wrapper will download Gradle automatically
- Or install Gradle manually: https://gradle.org/install/

### Error: "Could not find com.android.tools.build:gradle:8.1.0"
- Make sure you have internet connection
- Gradle will download the plugin automatically

### Error: "No connected devices"
- Connect your device via USB
- Enable USB debugging in developer options
- Verify with: `adb devices`

## Build Variants

### Debug Build
- **Purpose:** Development and testing
- **Features:**
  - Debug symbols included
  - No code shrinking (ProGuard disabled)
  - Faster build times
- **Command:** `./gradlew assembleDebug`

### Release Build
- **Purpose:** Production distribution
- **Features:**
  - Code shrinking with ProGuard/R8
  - Optimized for performance
  - No debug symbols
- **Command:** `./gradlew assembleRelease`

### Bundle Build (Recommended for Google Play)
- **Purpose:** Google Play Store upload
- **Features:**
  - Smaller download size
  - Dynamic feature delivery
  - Better for users
- **Command:** `./gradlew bundleRelease`
- **Output:** `app/build/outputs/bundle/release/app-release.aab`

## Performance Tips

### Faster Builds
1. Enable Gradle daemon:
   ```bash
   ./gradlew --daemon
   ```

2. Use offline mode (after first build):
   ```bash
   ./gradlew --offline assembleDebug
   ```

3. Enable build cache:
   ```properties
   # In gradle.properties
   org.gradle.caching=true
   ```

4. Use parallel builds:
   ```properties
   # In gradle.properties
   org.gradle.parallel=true
   org.gradle.workers.max=8
   ```

### Memory Settings

If you get out of memory errors, add to `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

## Clean Build

To start fresh:

```bash
./gradlew clean
./gradlew cleanBuildCache
```

## Check Dependencies

To see all dependencies:

```bash
./gradlew dependencies
```

## Runtime Smoke Test (emulator)

A build that compiles is not proof that it runs. `.github/workflows/emulator-smoke-test.yml`
boots an Android 11 emulator, installs **both** the debug and the release APK and
launches each one, then requires all of the following before it will pass:

| Check | Why |
| --- | --- |
| Process alive 15 s after launch | catches crashes and early exits |
| Activity reported `Displayed` | proves a window actually appeared |
| GL surface created + continuous frames | proves the render loop is running |
| App's own framebuffer readback | catches a black screen that still "renders" |
| No crash / native fault / ANR / render error in logcat | catches silent failures |

The framebuffer check is the important one: `Renderer` clears to black, so a
process that renders 600 frames per second can still be showing nothing at all.
`SceneRenderer` therefore reads its own output back with `glReadPixels`, logs
`SceneCheck warm=<n>% ...`, and the test fails when that is close to zero.

The logic lives in [`scripts/emulator-smoke.sh`](scripts/emulator-smoke.sh), with
the PNG statistics helper in [`scripts/screenshot_colours.py`](scripts/screenshot_colours.py).
Run the checker on any screenshot with:

```bash
python3 scripts/screenshot_colours.py screenshot.png
```

## What the app shows

On launch the app draws a built-in default scene — a gradient background with a
lit, rotating cube — plus a small build label in the top-left corner reading, for
example, `Both Engine 1.0.66 · rendering`. That label is the quickest way to
confirm on a real device which build is installed and that rendering is live.

## Diagnosing a crash on a real device

Uncaught exceptions are logged under the `EngineApplication` tag and written to
the app's private storage as `files/last_crash.txt`. With the device connected:

```bash
adb logcat -s EngineApplication GameActivity Renderer SceneRenderer
adb shell run-as com.bothengine.game cat files/last_crash.txt
```

---
## License

This project uses various open-source libraries. See [LICENSE](LICENSE) for details.

---

**Happy Building!** 🚀
