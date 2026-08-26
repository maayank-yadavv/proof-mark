# Deployment & Testing Guide

## 1. Prerequisites & Environment Configuration

### Required Toolchain
* **Android Studio:** Ladybug (2024.2.1) or newer
* **Build System:** Gradle 8.13+ with Kotlin DSL
* **Target SDK:** 35 (Android 15)
* **Minimum SDK:** 24 (Android 7.0 Nougat)
* **JDK:** Java 17

### Secrets & API Key Setup
The platform accesses external AI services via environment variables injected at build time into `BuildConfig`:

1. Configure your Gemini API Key in `.env` or AI Studio Secrets panel:
   ```env
   GEMINI_API_KEY=your_actual_gemini_api_key
   ```
2. For Firebase integration, set:
   ```env
   FIREBASE_WEB_CLIENT_ID=your_firebase_client_id.apps.googleusercontent.com
   FIREBASE_API_KEY=your_firebase_api_key
   ```

---

## 2. Compilation & Verification

### Compiling the Applet
To verify applet build integrity:
```bash
compile_applet
```

### Running Local Unit Tests & Robolectric Tests
The test suite validates the Legal Metrology Rules Engine, OCR parsing logic, and Product Intelligence resolution without requiring an emulator.

To execute unit tests:
```bash
gradle :app:testDebugUnitTest
```

Key test files:
* `LegalMetrologyRulesEngineTest.kt`: Tests MRP tax qualification, missing country of origin, net quantity SI unit formatting, and PDP font size calculations.
* `ProductIntelligenceServiceTest.kt`: Verifies multi-source data resolution, benchmark pricing checks, and candidate matching logic.
* `ExampleUnitTest.kt`: Basic unit test suite.
* `GreetingScreenshotTest.kt`: Visual UI component verification.

---

## 3. Sample Payloads for API & Integration Testing

Sample JSON payloads representing structured inspection inputs and outputs are available in `/docs/sample_payloads/`:

1. `inspection_payload_food_beverage.json`: Compliant food package payload with FSSAI verification.
2. `inspection_payload_cosmetics.json`: Cosmetic product payload with flagged MRP tax qualification, USP, and font size violations.
3. `compliance_report_schema.json`: Strict JSON schema definition.

---

## 4. Deployment Instructions

1. **Building Release APK:**
   ```bash
   gradle :app:assembleRelease
   ```
2. **Building Android App Bundle (AAB):**
   ```bash
   gradle :app:bundleRelease
   ```
3. **Database Migration Policy:**
   Room Database uses versioning with fallback destructiveness disabled for production safety. Schema updates are registered in `AppDatabase.kt`.
