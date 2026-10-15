# BreakTime ⏱️

**BreakTime** is a modern, privacy-focused, offline Android utility app designed to accurately calculate net working time by subtracting multiple break durations from a total work duration.

Built with **Kotlin** and **Jetpack Compose (Material 3)**, BreakTime offers a clean, responsive, and intuitive interface with zero ads, tracking, or network permissions required.

---

## ✨ Features

- 🕒 **Work Duration Input**: Enter work hours and minutes manually or use quick work presets (e.g., 4h, 7.5h, 8h, 8.5h, 12h).
- ☕ **Multiple Custom Breaks**: Add, label, and manage multiple break durations with quick presets (15m, 30m, 45m, 1h).
- 📐 **Automatic Normalization**: Automatically normalizes input minutes (e.g., converting 90 minutes into 1 hour and 30 minutes).
- 📊 **Net Work Time Breakdown**:
  - Net hours and remaining minutes
  - Total decimal hours (e.g., `7.25 hours`)
  - Digital timer format (`07:15:00`)
  - Break percentage & visual proportion bar
- 📋 **Easy Sharing**: One-tap copy to clipboard or native system share.
- 📜 **Recent Calculation History**: Automatically saves up to 10 recent calculations. Tap any entry in history to reload and recalculate instantly.
- 🎨 **Material 3 Design & Themes**:
  - Full Material 3 dynamic color support (Android 12+)
  - System default, Light, and Dark themes
  - Smooth Compose animations and haptic feedback
- ⚙️ **User Preferences**:
  - Persistent state option (preserves last calculation on restart)
  - Toggle haptics, animations, and deletion confirmation dialogs
- 🔒 **100% Offline & Private**: No internet permissions needed. All data is processed and stored locally on device.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern + Reactive `StateFlow`
- **Navigation**: Jetpack Navigation Compose
- **Data Persistence**: Local `SharedPreferences` with `Gson` serialization
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`) with Version Catalog (`libs.versions.toml`)
- **SDK Requirements**:
  - **Min SDK**: `26` (Android 8.0 Oreo)
  - **Target SDK**: `37` (Android 16)
  - **Compile SDK**: `37`

---

## 📂 Project Structure

```text
com.example.breaktime/
├── data/                  # Repositories for History and Settings persistence
├── domain/                # Business logic: Calculator, Formatter, Validator
├── model/                 # Data classes and UI state models
├── ui/                    # Jetpack Compose UI Components & Screens
│   ├── about/             # About screen (app details & features)
│   ├── history/           # Calculation history screen
│   ├── home/              # Main calculation screen & result card
│   ├── navigation/        # Bottom navigation routes and graphs
│   ├── settings/          # Preferences & settings screen
│   ├── splash/            # Animated splash screen
│   ├── theme/             # Material 3 color schemes, typography, and theme wrapper
│   └── viewmodel/         # BreakTimeViewModel & Factory
└── util/                  # Utility helpers (e.g., HapticUtils)
```

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Ladybug or newer recommended)
- JDK 11 or higher
- Android SDK 37 installed

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/BreakTime.git
   cd BreakTime
   ```

2. **Open in Android Studio**:
   Open Android Studio and select **Open** -> Navigate to the cloned `BreakTime` folder.

3. **Build & Run**:
   Sync Gradle and run the `:app` module on an Android Virtual Device (AVD) or physical Android device running Android 8.0 (API 26) or higher.

---

## 🧪 Testing

The app includes unit tests covering calculation domain logic, duration normalization, input validation, repository persistence, and ViewModel state transitions:

```bash
./gradlew test
```

---

## 📄 License

```text
Copyright (c) 2025 Abdullah Ateeq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
