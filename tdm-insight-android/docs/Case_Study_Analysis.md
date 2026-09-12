# Case Study Analysis — VancoTrack
## CDE2313 Mobile Application Development | Semester 3, 2025–2026

---

## 1. Problem Domain

A hospital pharmacy department performs Therapeutic Drug Monitoring (TDM) calculations for medicines with narrow therapeutic indices such as Vancomycin. These calculations are currently performed manually or using desktop-based tools, creating challenges in:

- **Accuracy** — manual formula application is prone to transcription and arithmetic errors
- **Efficiency** — calculations require multiple steps across different references
- **Accessibility** — desktop tools are not available at the point of care
- **Explainability** — results without step-by-step derivation are difficult to verify

**VancoTrack** addresses these challenges by providing a structured, validated, mobile-first calculation workflow with full explainability.

---

## 2. Requirements Analysis

### 2.1 Mandatory Functional Requirements

| # | Requirement | Implementation | File |
|---|---|---|---|
| R1 | Native Android using Kotlin and Jetpack Compose | 100% Compose UI, Kotlin 2.0 | All UI files |
| R2 | Vancomycin Pre workflow | PRE enum + conditional form + CrCl-based Ke calculation | `VancomycinCalculator.kt` |
| R3 | Vancomycin Post workflow | POST enum + back-calculated Ke from post concentration | `VancomycinCalculator.kt` |
| R4 | Vancomycin Pre+Post workflow | PRE_POST enum + log-linear Ke from two concentrations | `VancomycinCalculator.kt` |
| R5 | Dynamic input forms | Conditional `if(workflow == ...)` field rendering in Compose | `TdmScreens.kt` |
| R6 | Input validation | `TdmValidation.validate()` — 30+ rules, returns `List<String>` | `TdmValidation.kt` |
| R7 | Dedicated calculation engine | `VancomycinCalculator` class with zero Compose imports | `VancomycinCalculator.kt` |
| R8 | Pharmacokinetic outputs | Ke, t½, Vd, CL, trough, AUC24 as `ResultMetric` list | `VancomycinCalculator.kt` |
| R9 | Explainable results | `ExplanationScreen` renders `CalculationStep` trail with equations | `TdmScreens.kt` |
| R10 | Material 3 UI | `lightColorScheme`, `Typography`, `Shapes`, M3 components | `Theme.kt`, `TdmComponents.kt` |

### 2.2 Non-Functional Requirements

| Requirement | Implementation |
|---|---|
| No cloud/network dependency | No `INTERNET` permission; all data stored on-device |
| No user authentication | App starts directly to calculator |
| Offline operation | SharedPreferences + local PDF generation |
| Responsive UI | `LazyColumn`, `Scaffold`, `WindowInsets` padding |
| Clean architecture | MVVM + Repository + Domain separation |

### 2.3 Optional Enhancement Requirements

| # | Enhancement | Status | Notes |
|---|---|---|---|
| O1 | Local calculation history | ✅ Implemented | SharedPreferences, max 20 records, HistoryScreen with delete |
| O2 | Camera capture of lab report | ✅ Implemented | CameraX 1.4.1, permission handling, guide overlay |
| O3 | OCR-assisted extraction | ✅ Implemented | ML Kit, candidate list, mandatory confirmation |
| O4 | PDF export and sharing | ✅ Implemented | PdfDocument API, 2-page A4, FileProvider share |
| O5 | What-if simulation | ❌ Not implemented | Optional per case study; out of scope for this submission |
| O6 | Concentration-time graph | ❌ Not implemented | Optional per case study; out of scope for this submission |
| O7 | Additional drug module | ❌ Not implemented | Optional per case study; Vancomycin only as required |

---

## 3. System Architecture Analysis

### 3.1 Architecture Pattern: MVVM + Clean Architecture

```
Presentation Layer          Domain Layer            Data Layer
──────────────────          ────────────            ──────────
SplashScreen                TdmValidation           CalculationRepository
CalculatorScreen    ←──►    VancomycinCalc  ←──►    PdfExporter
ResultsScreen       TdmVM   CalculationStep          CalculationRecord
ExplanationScreen           TdmResult               SharedPreferences
HistoryScreen
CameraScreen
LabReportReviewScreen
```

### 3.2 Data Flow Pipeline

```
User Input (Form)
       ↓
TdmUiState (StateFlow in TdmViewModel)
       ↓
TdmInput (data class — typed model)
       ↓
TdmValidation.validate() → List<String> errors
       ↓ (if empty)
VancomycinCalculator.calculate() → Result<TdmResult>
       ↓
TdmResult (metrics + steps + warnings + modelNote)
       ↓
ResultsScreen + ExplanationScreen
```

### 3.3 Key Design Decisions

**Decision 1: Calculation engine as a pure class**
`VancomycinCalculator` has zero Android or Compose imports. It takes `TdmInput` and returns `Result<TdmResult>`. This makes it independently testable and ensures no business logic leaks into the UI.

**Decision 2: StateFlow-based reactive state**
`TdmViewModel` exposes a single `StateFlow<TdmUiState>`. All screens observe it via `collectAsState()`. This eliminates manual state synchronisation.

**Decision 3: On-device OCR instead of cloud**
ML Kit Text Recognition runs entirely on-device. No image data ever leaves the device. This is aligned with the no-cloud-backend requirement.

**Decision 4: Android PdfDocument for export**
Zero additional dependencies for PDF generation. The export produces a professional two-page A4 report with patient summary, PK metrics grid, calculation trail, and clinical warnings.

---

## 4. Validation Framework

### 4.1 Validation Categories

| Category | Example Rules |
|---|---|
| Required fields | Age, weight, creatinine, dose, interval, infusion must be valid positive numbers |
| Range validation | Age 18–120, dose 250–2000 mg, interval 6–72 hr, infusion 0.5–4 hr |
| Unit validation | Creatinine ≤ 15 mg/dL (warns if unusually high — likely unit error) |
| Cross-field | Infusion duration < dosing interval; post concentration > pre concentration (PRE_POST) |
| Timing logic | Post sample must be taken before next dose |
| Math protection | CrCl and Ke validated for finite positive values before use |
| Error vs review | Blocking errors prevent calculation; advisory warnings appear after results |

### 4.2 Error vs Warning Distinction

```
BLOCKING ERRORS (red, prevent calculation):
  → Infusion duration must be shorter than the dosing interval
  → Post-dose concentration must be higher than pre-dose
  → Age must be a positive number

ADVISORY WARNINGS (amber, shown after results):
  → CrCl below 30 mL/min — specialist review recommended
  → Measured and modelled trough differ materially — verify timing
  → Review equations from authoritative sources
```

---

## 5. Pharmacokinetic Model

### 5.1 Renal Function Estimation (Cockcroft-Gault)

```
CrCl (mL/min) = [(140 − Age) × Weight(kg)] / [72 × SCr(mg/dL)]  ×  0.85 (if female)
```

Reference: Cockcroft & Gault (1976), *Nephron*

### 5.2 Elimination Rate (Ke)

**PRE workflow (screening estimate):**
```
Ke (hr⁻¹) = 0.00083 × CrCl + 0.0044
```

**POST and PRE+POST workflow (log-linear):**
```
Ke = ln(C_post / C_pre) / t_between
```

### 5.3 Volume of Distribution

```
Vd (initial) = 0.7 × Weight (kg)
Vd (back-calculated from post) = [Dose × (1 − e^(−Ke × t_inf))] / [Ke × t_inf × C_peak]
```

### 5.4 Predicted Trough and AUC24

```
C_trough = C_peak × e^(−Ke × (τ − t_inf))

AUC₂₄ = (Dose × 24) / (CL × τ)       where CL = Ke × Vd
```

Target AUC₂₄/MIC ≥ 400 mg·hr/L per ASHP/IDSA 2020 Vancomycin guidelines.

---

## 6. User Flow Mapping (Case Study Section 4)

| Case Study Step | VancoTrack Implementation |
|---|---|
| 1. Open VancoTrack | SplashScreen with animated entrance |
| 2. Create a fictional case | CalculatorScreen with "Load Sample Case" button |
| 3. Enter patient parameters | Step 1 card: Age, Weight, Sex, Creatinine |
| 4. Select Vancomycin | Vancomycin is the only (and preselected) drug |
| 5. Select Pre / Post / Pre+Post | FilterChip workflow selector in Step 2 card |
| 6. Enter workflow values | Step 3 card dynamically shows relevant concentration fields |
| 7. Validate information | Validate & Calculate button → TdmValidation rules run |
| 8. Review calculation inputs | Error banners shown inline if validation fails |
| 9. Run TDM calculation | VancomycinCalculator.calculate() runs on button press |
| 10. Display intermediate results | ResultsScreen — PK metric grid |
| 11. Open calculation explanation | ExplanationScreen — step-by-step trail with equations |

---

## 7. Camera Feature Analysis (Case Study Section 11)

The implemented flow exactly matches the case study requirement:

```
Camera → Capture → Review → Confirm Value → Use in Calculation
```

| Step | Implementation |
|---|---|
| Camera | CameraX PreviewView, flash control, permission handling |
| Capture | `ImageCapture.takePicture()` → saved to `cache/camera/*.jpg` via FileProvider |
| Review | `LabReportReviewScreen` shows image + OCR candidates + manual field |
| Confirm | `WarningBanner` + explicit "Confirm & Use This Value" button — no auto-fill |
| Use in Calculation | `vm.confirmOcrValue(field, value)` injects only after explicit confirmation |

---

## 8. Alignment Summary

| Case Study Requirement | Status |
|---|---|
| All mandatory functional requirements | ✅ 10/10 implemented |
| Dynamic forms per workflow | ✅ |
| Validation beyond empty-field check | ✅ 7 validation categories |
| Calculation engine separated from UI | ✅ Zero Compose imports in domain layer |
| Explainable results with equations | ✅ |
| Material 3 UI | ✅ |
| No authentication | ✅ Confirmed absent |
| No cloud backend | ✅ Confirmed absent |
| Optional: Camera + OCR + PDF export + History | ✅ 4/7 optional features |

**Overall alignment: FULL compliance with all mandatory requirements. Partial compliance with optional enhancements (4 of 7 implemented).**
