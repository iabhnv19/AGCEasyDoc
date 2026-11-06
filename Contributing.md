# Contributing to AGC EasyDoc

Thank you for your interest in contributing to **AGC EasyDoc**! Whether you're fixing a bug, translating UI text, adding a new feature, or improving documentation, your help is greatly appreciated.

Please follow these guidelines to ensure a smooth contribution process.

---

## Code of Conduct

By participating in this project, you agree to abide by our community standards:
- Be respectful, welcoming, and constructive in discussions and pull requests.
- Focus on what is best for the community and users of academic document tooling.

---

## Getting Started

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer recommended)
- **Android SDK** (compileSdk / targetSdk configured in `app/build.gradle.kts`)
- Java Development Kit (JDK 17+)
- An Android Emulator or physical Android device (API 26+)

### Setting Up the Project
1. Clone the repository:
   ```bash
   git clone https://github.com/iabhnv19/AGCEasyDoc.git
   ```
2. Open Android Studio and select **Open**, pointing to the `AGCEasyDoc` root directory.
3. Allow Gradle to sync dependencies (`libs.versions.toml`).
4. Build the project:
   ```bash
   ./gradlew assembleDebug
   ```
5. Run the app on your connected device or emulator.

---

## Architecture & Project Structure

AGC EasyDoc is built with modern Android development practices:
- **UI Toolkit:** Jetpack Compose with Material 3 design.
- **Architecture:** MVVM (Model-View-ViewModel) with `DocumentViewModel` managing document state and persistence.
- **Local Storage:** Room Database (`AppDatabase`, `DocumentDao`) for persisting saved documents.
- **Navigation:** Custom Composable navigation via `AppNav.kt`.
- **Export & Print:** PDF generation (`PdfMetadataWriter.kt`, `PrintUtils.kt`) and image processing (`ImageUtils.kt`).

Key directories under `app/src/main/java/com/example/agceasydoc/`:
- `data/`: Database entities, DAO, ViewModel, and data models.
- `screens/`: Compose UI screens (`HomeScreen.kt`, `CustomizeDocScreen.kt`, `DocumentPreview.kt`, etc.).
- `nav/`: Navigation graph (`AppNav.kt`).
- `print/`: PDF generation and printing utilities.

---

## How to Contribute

### 1. Reporting Issues
- Check existing issues before opening a new one.
- Provide clear steps to reproduce bugs, along with device information and Android version.

### 2. Submitting Pull Requests
1. Fork the repository and create your feature branch from `main` (`git checkout -b feature/my-new-feature`).
2. Make your code changes following Kotlin coding conventions and existing Jetpack Compose patterns.
3. Test your changes thoroughly on an emulator or device.
4. Run Gradle build and unit/instrumentation tests to ensure no regressions:
   ```bash
   ./gradlew test
   ```
5. Push to your fork and submit a Pull Request with a clear description of the changes.

---

## License

By contributing to AGC EasyDoc, you agree that your contributions will be licensed under the [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE).
