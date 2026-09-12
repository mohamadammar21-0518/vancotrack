# VancoTrack — Vancomycin Therapeutic Drug Monitoring Calculator

<p align="center">
  <img src="screenshots/splash.png" width="200" alt="Splash Screen"/>
  <img src="screenshots/calculator.png" width="200" alt="Calculator Screen"/>
  <img src="screenshots/results.png" width="200" alt="Results Screen"/>
</p>

---

## Project Title
**VancoTrack** — Native Android Vancomycin Therapeutic Drug Monitoring Calculator

---

## Course Information

| Field | Details |
|---|---|
| Course Code | CDE2313 |
| Course Name | Mobile Application Development |
| Semester / Year | Semester 3, 2025–2026 |
| Instructor (Lead) | Ts. Mohd Zulkifli Mohd Zaki |
| Instructor (Co-Lead) | Madam Siti Shafrah Shahawai |
| Case Study | VancoTrack — Week 4 |

---

## Group Members

| Name | Student ID |
|---|---|
| Mohamad Ammar | AIU24102204 |
| Mohamed Shohan | AIU24102201 |
| Harish Ramalingam | AIU24102348 |

---

## Case Study — Problem Overview

Hospital pharmacy departments perform Therapeutic Drug Monitoring (TDM) calculations for medicines such as Vancomycin. These calculations require patient demographics, medication dosing parameters, drug concentrations, and sampling timing. Performing these calculations manually is time-consuming and error-prone.

**VancoTrack** brings these inputs and calculations together into a structured, reliable native Android experience. The app allows clinicians and pharmacists to select a Vancomycin monitoring workflow, enter patient and dosing data, validate inputs, run pharmacokinetic calculations, and review step-by-step explainable results — all on a mobile device, entirely offline.

### Implemented Solutions

- **Three Vancomycin workflows** (Pre, Post, Pre+Post) with dynamic forms that show only relevant fields
- **Comprehensive validation** — 30+ rules covering required fields, clinical ranges, cross-field timing, and math-error protection
- **Pharmacokinetic calculation engine** fully separated from the UI layer
- **Explainable results** — step-by-step calculation trail with actual equations and intermediate values
- **Camera + OCR** — capture lab report image, extract numeric values on-device (ML Kit), mandatory review and confirmation before injecting into calculation
- **PDF export** — two-page A4 report generated via Android PdfDocument API, shareable via any app
- **Calculation history** — up to 20 recent calculations stored locally with per-record delete
- **Animated splash screen** and smooth screen-to-screen navigation transitions

---

## Key Implemented Features

### Core (Mandatory)
| Feature | Status |
|---|---|
| Vancomycin PRE workflow | ✅ |
| Vancomycin POST workflow | ✅ |
| Vancomycin PRE+POST workflow | ✅ |
| Dynamic forms per workflow | ✅ |
| Input validation (7 categories) | ✅ |
| Dedicated calculation engine (domain layer) | ✅ |
| PK outputs: Ke, t½, Vd, CL, Trough, AUC24 | ✅ |
| Explainable results with equations | ✅ |
| Material 3 UI | ✅ |

### Optional Enhancements
| Feature | Status |
|---|---|
| Local calculation history (up to 20 records) | ✅ |
| Camera capture of lab report | ✅ |
| On-device OCR (ML Kit) with mandatory confirmation | ✅ |
| PDF export and share | ✅ |
| Concentration-time graph | ❌ Out of scope |
| What-if simulation | ❌ Out of scope |
| Additional drug modules | ❌ Out of scope |

---

## Technology Stack and Application Architecture

### Technology Stack

| Component | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI Framework | Jetpack Compose (BOM 2024.12.01) |
| Design System | Material 3 |
| Architecture | MVVM + Repository Pattern |
| Navigation | Navigation Compose 2.8.5 |
| Camera | CameraX 1.4.1 |
| OCR | ML Kit Text Recognition 16.0.1 |
| Image Loading | Coil 2.7.0 |
| Local Storage | SharedPreferences (JSON) |
| PDF Generation | Android PdfDocument API |
| Permissions | Accompanist Permissions 0.36.0 |
| Build System | Gradle (Kotlin DSL) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |

### Application Architecture

```
┌─────────────────────────────────────────────────────┐
│                    UI LAYER                         │
│  SplashScreen  CalculatorScreen  ResultsScreen      │
│  ExplanationScreen  HistoryScreen  CameraScreen     │
│  LabReportReviewScreen                              │
└──────────────────────┬──────────────────────────────┘
                       │ StateFlow / events
┌──────────────────────▼──────────────────────────────┐
│                 VIEWMODEL LAYER                     │
│  TdmViewModel — orchestrates all state              │
│  OcrState / ExportState / TdmUiState                │
└──────┬──────────────────┬──────────────────┬────────┘
       │                  │                  │
┌──────▼──────┐  ┌────────▼────────┐  ┌─────▼───────┐
│  DOMAIN     │  │  DOMAIN         │  │  DATA       │
│  TdmValid.  │  │  VancomycinCalc │  │  Calc.Repo  │
│  (object)   │  │  (class)        │  │  PdfExporter│
└─────────────┘  └─────────────────┘  └─────────────┘
```

**Data Flow:**
```
UI → TdmUiState → TdmValidation → VancomycinCalculator → TdmResult → ResultsScreen
```

---

## Installation Guide

### Prerequisites
- Android Studio Ladybug (2024.2.1) or later
- JDK 17
- Android device or emulator running API 26+

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/[your-username]/tdm-insight-android.git
   cd tdm-insight-android
   ```

2. Open the project in Android Studio:
   - **File → Open** → select the `tdm-insight-android` inner folder (the one containing `app/` and `build.gradle.kts`)

3. Wait for Gradle sync to complete (first sync may take a few minutes to download dependencies)

4. Connect a device or start an emulator (API 26+)

5. Press **Shift + F10** or click **Run → Run 'app'**

---

## How to Build the Project

### Debug Build
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

> **Note:** Release build requires a signing keystore. See Android documentation for configuring a signing config in `build.gradle.kts`.

---

## APK Download

A pre-built release APK is available in the [`apk/`](apk/) directory:

> **[Download app-release.apk](apk/app-release.apk)**

Or install directly via ADB:
```bash
adb install apk/app-release.apk
```

---

## Screenshots

| Splash | Calculator | Results |
|---|---|---|
| ![Splash](screenshots/splash.png) | ![Calculator](screenshots/calculator.png) | ![Results](screenshots/results.png) |

| Explanation | History | Camera |
|---|---|---|
| ![Explanation](screenshots/explanation.png) | ![History](screenshots/history.png) | ![Camera](screenshots/camera.png) |

| Lab Review | PDF Export | |
|---|---|---|
| ![Lab Review](screenshots/lab_review.png) | ![PDF](screenshots/pdf_export.png) | |

---

## GitHub Repository Structure

```
tdm-insight-android/
│
├── README.md                          # Project landing page (this file)
├── LICENCE                            # MIT Licence
├── .gitignore                         # Android + Gradle gitignore rules
│
├── app/                               # Android Studio project root
│   ├── build.gradle.kts
│   └── src/main/java/my/edu/aiu/app/tdminsight/
│       ├── MainActivity.kt
│       ├── data/
│       │   ├── export/PdfExporter.kt
│       │   └── local/
│       │       ├── CalculationRecord.kt
│       │       └── CalculationRepository.kt
│       ├── domain/
│       │   ├── calculator/VancomycinCalculator.kt
│       │   └── validation/TdmValidation.kt
│       ├── model/
│       │   ├── OcrModels.kt
│       │   ├── Patient.kt
│       │   ├── TdmInput.kt
│       │   └── TdmResult.kt
│       ├── ui/
│       │   ├── components/TdmComponents.kt
│       │   ├── navigation/TdmNavigation.kt
│       │   ├── screens/
│       │   │   ├── SplashScreen.kt
│       │   │   ├── TdmScreens.kt
│       │   │   ├── CameraScreen.kt
│       │   │   ├── LabReportReviewScreen.kt
│       │   │   └── HistoryScreen.kt
│       │   └── theme/Theme.kt
│       └── viewmodel/TdmViewModel.kt
│
├── gradle/                            # Gradle wrapper
├── screenshots/                       # App screenshots (PNG)
├── docs/
│   ├── Case_Study_Analysis.md         # Requirements analysis and alignment
│   ├── wireframe/                     # UI wireframes
│   └── diagrams/
│       └── architecture.md            # Architecture diagrams
│
├── apk/
│   └── app-release.apk                # Pre-built release APK
│
├── presentation/
│   ├── Presentation.pptx
│   └── Presentation.pdf
│
├── ai/
│   └── AI_Usage_Log.pdf               # AI tool usage declaration
│
└── assets/                            # Supporting resources
```

---

## Acknowledgements

- **Ts. Mohd Zulkifli Mohd Zaki** — Lead Instructor, CDE2313
- **Madam Siti Shafrah Shahawai** — Co-Lead Instructor, CDE2313
- **myTDM Calculator** (https://www.mytdmcalculator.com/) — Reference application for TDM workflow understanding
- **Malaysian Pharmacy Information System (PhIS)** — TDM Calculator documentation for Vancomycin workflow reference
- **Google ML Kit** — On-device OCR text recognition
- **JetBrains / Google** — Kotlin, Jetpack Compose, Android Studio

---

## References

1. Cockcroft, D.W. & Gault, M.H. (1976). Prediction of creatinine clearance from serum creatinine. *Nephron, 16*(1), 31–41.
2. Rybak, M.J. et al. (2020). Therapeutic monitoring of vancomycin for serious methicillin-resistant *Staphylococcus aureus* infections: A revised consensus guideline and review by the American Society of Health-System Pharmacists, the Infectious Diseases Society of America, the Pediatric Infectious Diseases Society, and the Society of Infectious Diseases Pharmacists. *American Journal of Health-System Pharmacy, 77*(11), 835–864.
3. Android Developers Documentation — https://developer.android.com/docs
4. Jetpack Compose Documentation — https://developer.android.com/compose
5. Material Design 3 — https://m3.material.io/

---

*VancoTrack is an application developed for educational and software development purposes as part of course CDE2313. It must not be used for clinical prescribing, diagnosis, or treatment decisions.*
