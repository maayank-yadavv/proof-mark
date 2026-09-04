<div align="center">

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
<img src="https://img.shields.io/badge/Project-Student%20Project-6A0DAD?style=for-the-badge&logo=graduation-cap&logoColor=white"/>
<img src="https://img.shields.io/badge/minSdk-24-orange?style=for-the-badge"/>

<br/><br/>

# 🔏 Proof Mark

### Smart Legal Metrology Compliance Platform — A Student Project

**Proof Mark** is an Android app built as a student project that helps enforcement officers and consumers verify packaged commodity compliance under India's **Legal Metrology (Packaged Commodities) Rules, 2011**. It uses OCR, smart rule checking, and camera-based scanning to detect statutory violations in real time.

</div>

---

## ✨ Features

| Feature | Description |
|---|---|
| 📷 **Camera Inspection** | Live camera scanning of product labels with ML Kit OCR |
| 🤖 **Smart AI Analysis** | Automated compliance verdict generation using on-device intelligence |
| 🔍 **Barcode / QR Scanning** | ML Kit barcode scanning for quick product lookup |
| 📋 **Deterministic Rule Engine** | Rule checks for MRP, net quantity, MFD, USP, and customer care details |
| 🧾 **Evidence Audit Trail** | Full inspection history with compliance status per record |
| 📊 **Dashboard Analytics** | Metrics for total audits, compliance rate, violations, and pending reviews |
| 🗄️ **FSSAI Database** | Quick-access FSSAI product verification database |
| 🔒 **Dual Role Mode** | Enforcement Officer terminal and Consumer Package Portal in one app |
| 🌙 **Dark / Light Theme** | Dynamic theme toggle with Material 3 design |
| 📶 **Network Status Indicator** | Real-time connectivity monitoring with ping check |
| 🧪 **Demo Presets** | Preloaded mock inspection cases for training and testing |

---

## 📸 App Screens

> **Dashboard** → **Camera Inspection** → **Inspection Detail** → **Audit History** → **Metrology Rules** → **Settings**

The app supports two user roles:

- **Enforcement Officer** — Full terminal access with infraction alerts, audit history, FSSAI DB, and metrology rules.
- **Standard Consumer** — Simplified product scan portal to verify label declarations before purchase.

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (ViewModel + StateFlow) |
| **AI** | Cloud-based Language Model API (via Firebase AI Logic) |
| **OCR** | ML Kit Text Recognition |
| **Barcode** | ML Kit Barcode Scanning |
| **Camera** | CameraX (camera2) |
| **Database** | Room (SQLite) |
| **Networking** | Retrofit + OkHttp + Moshi |
| **Image Loading** | Coil Compose |
| **Persistence** | DataStore Preferences |
| **Auth** | Firebase Authentication + Sign-In (Credential Manager) |
| **Backend** | Firebase Firestore + Firebase App Check |
| **Testing** | Robolectric + Roborazzi + Espresso |
| **Build** | KSP, Secrets Gradle Plugin |

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Meerkat or later recommended)
- Android device or emulator running **API 24+**
- An AI API key (see [`.env.example`](.env.example) for the variable name)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/proof-mark.git
   cd proof-mark
   ```

2. **Open in Android Studio**
   Select **File → Open** and choose the cloned directory.
   Wait for Gradle sync to complete. Allow Android Studio to resolve any compatibility issues.

3. **Configure your API key**

   Create a `.env` file in the **project root** (next to `settings.gradle.kts`):
   ```env
   AI_API_KEY=your_api_key_here
   ```
   > See [`.env.example`](.env.example) for the full list of supported variables.

4. **Fix the signing config for debug builds**

   In `app/build.gradle.kts`, remove or comment out this line inside `buildTypes > debug`:
   ```diff
   - signingConfig = signingConfigs.getByName("debugConfig")
   ```

5. **Run the app**
   Select an emulator or connect a physical device, then click **Run ▶** or press `Shift+F10`.

---

## 📁 Project Structure

```
proof-mark/
├── app/
│   └── src/main/java/com/example/
│       ├── data/
│       │   ├── ai/           # AI integration & mock samples
│       │   ├── db/           # Room database & DAOs
│       │   ├── models/       # Data models (ComplianceStatus, UserRole…)
│       │   └── repository/   # Repository layer
│       ├── ui/
│       │   ├── components/   # Shared Compose components
│       │   ├── screens/      # Feature screens (Dashboard, Camera, History…)
│       │   ├── theme/        # Material 3 theme & color tokens
│       │   └── viewmodel/    # InspectionViewModel & state holders
│       ├── utils/            # Utility helpers
│       └── MainActivity.kt  # App entry point & Navigation host
├── assets/                   # Static assets
├── docs/                     # Documentation & screenshots
├── .env.example              # Environment variable template
├── build.gradle.kts          # Root build config
└── settings.gradle.kts       # Module settings
```

---

## ⚖️ Legal Metrology Rules Checked

Proof Mark verifies the following mandatory declarations under the **LM (Packaged Commodities) Rules, 2011**:

- ✅ **MRP** — Maximum Retail Price (inclusive of all taxes)
- ✅ **Net Quantity** — Weight, volume, or count as applicable
- ✅ **Unit Sale Price (USP)**
- ✅ **Date of Manufacture / Packing**
- ✅ **Best Before / Expiry Date** (where applicable)
- ✅ **Manufacturer / Packer Name & Address**
- ✅ **Country of Origin**
- ✅ **Consumer Care Contact Details**
- ✅ **FSSAI License Number** (food products)

---

## 🧪 Running Tests

```bash
# Unit tests (Robolectric + Roborazzi screenshot tests)
./gradlew test

# Instrumented tests (Espresso)
./gradlew connectedAndroidTest
```

---

## 🔑 Environment Variables

| Variable | Required | Description |
|---|---|---|
| `AI_API_KEY` | ✅ Yes | Your AI API key for compliance analysis |
| `FIREBASE_APPCHECK_DEBUG_TOKEN` | Optional | Firebase App Check debug token for local dev |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'feat: add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Built with ❤️ by a student developer using **Jetpack Compose**, **ML Kit**, and **Firebase**

</div>
