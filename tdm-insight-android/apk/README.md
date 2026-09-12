# APK

Place the signed release APK here before submission.

## Expected file
`app-release.apk`

## How to generate

### Option 1: Android Studio
1. **Build → Generate Signed Bundle / APK**
2. Select **APK**
3. Create or select a keystore
4. Choose **release** build variant
5. Click **Finish**
6. Copy the output APK to this folder

### Option 2: Command line
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

Then sign with `apksigner`:
```bash
apksigner sign --ks my-key.jks --out apk/app-release.apk app-release-unsigned.apk
```

## Install via ADB
```bash
adb install apk/app-release.apk
```
