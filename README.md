# RhymeKeyboard - Android IME with Rhyme Suggestions

A complete Android Input Method Editor (IME) that provides real-time rhyme suggestions while typing. This custom keyboard works system-wide across all apps and uses the Datamuse API to fetch rhyme data.

## Features

- **System-wide keyboard**: Works across all Android apps
- **Real-time rhyme suggestions**: Shows rhymes after pressing Enter
- **Aggregate mode**: Toggle to combine rhymes from all previous line-end words
- **Dark theme**: Minimal, brandable design with neon orange accents
- **QWERTY layout**: Standard keyboard layout with custom styling

## Project Structure

```
RhymeKeyboard/
├─ settings.gradle.kts
├─ build.gradle.kts               (Project)
├─ gradle.properties
├─ app/
│  ├─ build.gradle.kts            (Module)
│  └─ src/
│     ├─ main/
│     │  ├─ AndroidManifest.xml
│     │  ├─ java/com/antih3ro/rhymekeyboard/
│     │  │  ├─ RhymeKeyboardService.kt
│     │  │  ├─ RhymeClient.kt
│     │  │  └─ Utils.kt
│     │  └─ res/
│     │     ├─ layout/
│     │     │  └─ keyboard_layout.xml
│     │     ├─ xml/
│     │     │  ├─ method.xml
│     │     │  └─ qwerty.xml
│     │     ├─ values/
│     │     │  ├─ colors.xml
│     │     │  ├─ strings.xml
│     │     │  └─ styles.xml
│     │     └─ drawable/
│     │        └─ key_bg.xml
```

## How to Build & Run

1. **Open Android Studio** → New Project → No Activity (or Empty Activity; we don't use it) → Kotlin
2. **Replace the auto-generated files** with the files in this project (match paths)
3. **Sync Gradle** → Build
4. **Install on device/emulator**
5. **On device**: Settings → System → Languages & input → On-screen keyboard → Manage keyboards → Enable "Rhyme Keyboard"
6. **Switch to it** (tap keyboard switch icon). Type anywhere. After you hit Enter, rhymes appear for the last word of the previous line. Toggle Aggregate to blend rhyme lists from all previous line-end words.

## How It Works

### Core Components

1. **RhymeKeyboardService.kt**: Main IME service that handles keyboard input and manages the UI
2. **RhymeClient.kt**: HTTP client that fetches rhyme data from Datamuse API
3. **Utils.kt**: Text processing utilities for extracting words from typed content
4. **keyboard_layout.xml**: Custom keyboard layout with suggestion bar and controls

### Key Features

- **Text Analysis**: Monitors what you type and extracts the last word of previous lines
- **API Integration**: Uses Datamuse API (`https://api.datamuse.com/words?rel_rhy=`) for rhyme data
- **Suggestion Display**: Shows rhyme suggestions in a horizontal scrollable bar
- **Aggregate Mode**: Can combine rhymes from multiple words for richer suggestions
- **Dark Theme**: Professional dark appearance with rounded keys

### Technical Details

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Dependencies**: 
  - Kotlin Coroutines for async operations
  - OkHttp for HTTP requests
  - Material Design components
- **Permissions**: INTERNET (for API calls)

## Usage Notes

- **API Requirements**: Uses HTTPS (Datamuse). You already have INTERNET permission
- **Suggestion Trigger**: Make sure there's at least one previous line and its last token is a word (letters/apostrophes)
- **Context Window**: Uses `getTextBeforeCursor(2000, 0)` - can be increased if you need more context
- **Rate Limiting**: Respects Datamuse API fair use guidelines

## Customization Options

### Branding (ANTiHERo Theme)
- Neon-orange accents already defined in `colors.xml`
- Custom font support can be added
- Logo can be added to suggestion bar
- Key styling in `key_bg.xml`

### Additional Features
- **Symbols keyboard**: Add `res/xml/symbols.xml` and switch with -2 key
- **Shift state visuals**: Swap to uppercase key labels when shift=true
- **Offline mode**: Bundle CMU dictionary; build phonetic index; add slant-rhyme
- **Performance**: Prefetch on pause/commas or when user lingers after a line
- **iOS port**: UIInputViewController with accessory bar for rhymes

## License & Credits

- **Datamuse API** for rhyme data. Respect their fair use guidelines.
- Consider adding a local cache for frequently used words.

---

*This is your launchpad — a lean, dark keyboard that whispers rhymes while you write. Flip the switch, and let the lines find their echoes.*
