# How to Import RhymeKeyboard into Android Studio

## Prerequisites

- Android Studio installed (latest version recommended)
- Android SDK installed
- Java/Kotlin development environment set up

## Step-by-Step Import Process

### 1. Open Android Studio
- Launch Android Studio
- If you see the Welcome screen, proceed to step 2
- If you have another project open, go to `File` → `Close Project` to get to the Welcome screen

### 2. Import the Project
- On the Welcome screen, click **"Open"** (or **"Open an existing Android Studio project"**)
- Navigate to: `C:\Users\Administrator\Desktop\RhymeKeyboard`
- Select the **RhymeKeyboard** folder (the one containing `build.gradle.kts` and `settings.gradle.kts`)
- Click **"OK"**

### 3. Wait for Gradle Sync
- Android Studio will automatically start syncing the project
- You'll see "Gradle sync in progress..." in the status bar
- This may take a few minutes on first import
- Wait for the sync to complete successfully

### 4. Verify Project Structure
After sync completes, you should see this structure in the Project panel:
```
RhymeKeyboard/
├── app/
│   ├── src/main/
│   │   ├── java/com/antih3ro/rhymekeyboard/
│   │   │   ├── RhymeKeyboardService.kt
│   │   │   ├── RhymeClient.kt
│   │   │   └── Utils.kt
│   │   ├── res/
│   │   │   ├── drawable/key_bg.xml
│   │   │   ├── layout/keyboard_layout.xml
│   │   │   ├── values/colors.xml, strings.xml, styles.xml
│   │   │   └── xml/method.xml, qwerty.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

### 5. Build the Project
- Once Gradle sync is complete, build the project:
- Go to `Build` → `Make Project` (or press `Ctrl+F9`)
- Wait for the build to complete
- Check the Build panel for any errors

### 6. Run/Install the App
- Connect an Android device or start an emulator
- Click the **Run** button (green triangle) or press `Shift+F10`
- Select your target device
- The app will be installed on your device

### 7. Enable the Keyboard
After installation, you need to enable the keyboard:
1. On your Android device, go to **Settings**
2. Navigate to **System** → **Languages & input** → **On-screen keyboard**
3. Tap **Manage keyboards**
4. Find **"Rhyme Keyboard"** and toggle it ON
5. You may see a security warning - tap **OK** to enable

### 8. Use the Keyboard
- Open any app that requires text input (Messages, Notes, etc.)
- Tap in a text field to bring up the keyboard
- Tap the keyboard switcher icon (usually bottom-right)
- Select **"Rhyme Keyboard"**
- Start typing! After pressing Enter, you'll see rhyme suggestions

## Troubleshooting

### If Gradle Sync Fails:
- Check your internet connection (needed to download dependencies)
- Go to `File` → `Sync Project with Gradle Files`
- Try `Build` → `Clean Project` then `Build` → `Rebuild Project`

### If Build Fails:
- Check the Build panel for specific error messages
- Ensure you have the correct Android SDK version (API 34)
- Update Android Studio if needed

### If Keyboard Doesn't Appear:
- Make sure you enabled it in device settings
- Restart the app you're trying to type in
- Check that the keyboard service is running in device settings

## Project Features
- **System-wide keyboard**: Works in all Android apps
- **Rhyme suggestions**: Uses Datamuse API for real-time rhymes
- **Aggregate mode**: Combines rhymes from multiple words
- **Dark theme**: Professional appearance with rounded keys
- **QWERTY layout**: Standard keyboard with custom styling

## Next Steps
- Customize the theme colors in `res/values/colors.xml`
- Add more keyboard layouts in `res/xml/`
- Enhance the UI in `res/layout/keyboard_layout.xml`
- Extend functionality in the Kotlin source files

Happy coding! 🎹✨
