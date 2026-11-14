# Mobile Application Local Setup Guide

> Fast-track guide for setting up and running the BOLD Android Native Trading Platform (Kotlin + Jetpack Compose). For full monorepo setup instructions, see the master [`SETUP.md`](file:///d:/trading%20app/SETUP.md).

---

## 1. Prerequisites
* **JDK**: OpenJDK 17 or 21 (Temurin) — check with `java -version`
* **Android Studio**: Ladybug (2024.2+) or newer
* **Android SDK**: API Level 34 (Android 14) and Android SDK Command-line Tools
* **Android Emulator / Device**: Running Android 10+ (API 29+)

---

## 2. Quick Setup (3 Steps)

1. **Open the project in Android Studio:**
   * Select **Open** $\rightarrow$ select `d:\trading app\mobile-application`.
   * Allow Gradle to download dependencies and sync the multi-module project.

2. **Configure `local.properties`:**
   Ensure `mobile-application/local.properties` exists with:
   ```properties
   sdk.dir=C\:\\Users\\Hp\\AppData\\Local\\Android\\Sdk
   bold.api.base.url=http://10.0.2.2:8000/v1/
   bold.ws.base.url=ws://10.0.2.2:8000/v1/stream/market
   bold.data.mode=mock
   bold.free.tier.override=true
   ```
   *(On Android emulators, `10.0.2.2` routes to host PC's `localhost:8000`)*.

3. **Assemble and Run:**
   * Click the green **Run ▶** button in Android Studio, or execute from terminal:
   ```bash
   # Windows (PowerShell)
   .\gradlew.bat assembleDebug
   .\gradlew.bat installDebug

   # macOS / Linux
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

---

## 3. Available Gradle Tasks

| Task | Command | Purpose |
| :--- | :--- | :--- |
| **Debug Build** | `./gradlew assembleDebug` | Generates debug APK in `app/build/outputs/apk/debug/`. |
| **Unit Tests** | `./gradlew test` | Executes all JUnit 5 & MockK unit tests across modules. |
| **UI Tests** | `./gradlew connectedAndroidTest` | Runs Compose UI tests on a connected device/emulator. |
| **Lint Analysis** | `./gradlew lintDebug` | Analyzes code quality and accessibility standards. |

---

## 4. Common Troubleshooting

* **`adb` not recognized:** Add `Android\Sdk\platform-tools` to your system `PATH`.
* **Cannot connect to backend (`Connection Refused`):** Use `10.0.2.2:8000` in the emulator, not `localhost:8000`.
* **Gradle JDK mismatch:** In Android Studio, go to **Settings $\rightarrow$ Build Tools $\rightarrow$ Gradle** and set **Gradle JDK** to JDK 17 or 21.
