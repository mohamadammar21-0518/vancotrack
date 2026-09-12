# VancoTrack — Architecture Diagrams

---

## 1. Overall Application Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                          │
│                                                                     │
│  ┌────────────┐  ┌──────────────────┐  ┌────────────────────────┐  │
│  │ SplashScreen│  │ CalculatorScreen │  │    ResultsScreen       │  │
│  └────────────┘  └──────────────────┘  └────────────────────────┘  │
│  ┌──────────────────────┐  ┌─────────────┐  ┌──────────────────┐  │
│  │  ExplanationScreen   │  │HistoryScreen│  │  CameraScreen    │  │
│  └──────────────────────┘  └─────────────┘  └──────────────────┘  │
│  ┌───────────────────────────┐                                      │
│  │   LabReportReviewScreen   │                                      │
│  └───────────────────────────┘                                      │
│                                                                     │
│  Shared: TdmComponents.kt (ErrorBanner, MetricCard, TdmField, etc.) │
└────────────────────────┬────────────────────────────────────────────┘
                         │ collectAsState() / events
┌────────────────────────▼────────────────────────────────────────────┐
│                         VIEWMODEL LAYER                             │
│                                                                     │
│  TdmViewModel (AndroidViewModel)                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  TdmUiState (StateFlow)                                     │   │
│  │  • Form fields (age, weight, creatinine, dose…)             │   │
│  │  • errors: List<String>                                     │   │
│  │  • result: TdmResult?                                       │   │
│  │  • isCalculating: Boolean                                   │   │
│  │  • capturedImageUri / ocrState / selectedOcrValue           │   │
│  │  • exportState: ExportState                                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  OcrState: Idle | Running | Done(candidates) | Error                │
│  ExportState: Idle | Generating | Ready(uri) | Error                │
└──────┬────────────────────┬──────────────────┬─────────────────────┘
       │                    │                  │
┌──────▼──────┐  ┌──────────▼──────────┐  ┌───▼─────────────────────┐
│   DOMAIN    │  │       DOMAIN        │  │       DATA LAYER        │
│   LAYER     │  │       LAYER         │  │                         │
│             │  │                     │  │  CalculationRepository  │
│ TdmValidation  │  VancomycinCalculator│  │  (SharedPreferences)    │
│ object      │  │  class              │  │                         │
│             │  │                     │  │  PdfExporter            │
│ validate()  │  │  calculate()        │  │  (PdfDocument API)      │
│ → List<Str> │  │  → Result<TdmResult>│  │                         │
└─────────────┘  └─────────────────────┘  └─────────────────────────┘
```

---

## 2. Navigation Graph

```
[splash] ──────────────────────────────────────► [calculator]
                                                      │
                          ┌───────────────────────────┤
                          │                           │
                          ▼                           ▼
                      [history]               [camera] ──► [review]
                                                              │
                                                              ▼
                                                        [calculator]
                                                       (value injected)
                          ┌────────────────────────────────────────────┐
                          │                                            │
                          ▼                                            │
[calculator] ─────────► [results] ──────────────────► [explanation]   │
     ▲                      │                              │           │
     │                      │ onReset                      │ onBack    │
     └──────────────────────┘                              └───────────┘

Transition animations:
  splash → calculator : slideOut Left
  calculator → results : slideIn Left
  results → explanation : slideIn Up  (slideOut Down on back)
  calculator → camera : slideIn Up  (slideOut Down on back)
  camera → review : slideIn Left
  calculator → history : slideIn Left
```

---

## 3. Calculation Engine Data Flow

```
TdmUiState (strings)
       │
       │ TdmUiState.toInput()
       ▼
TdmInput (typed Double values)
 ├── workflow: Workflow (PRE | POST | PRE_POST)
 ├── patient: Patient (age, sex, weight, creatinine)
 ├── doseMg, intervalHr, infusionHr
 └── preConcentrationMgL?, postConcentrationMgL?
       │
       │ TdmValidation.validate(input)
       ▼
List<String> errors  ──► if not empty → show ErrorBanner → stop
       │ (if empty)
       │ VancomycinCalculator.calculate(input)
       ▼
┌─────────────────────────────────────────────────────┐
│ VancomycinCalculator                                │
│                                                     │
│  1. Cockcroft-Gault CrCl                            │
│  2. Ke (screening or log-linear)                    │
│  3. Vd (initial or back-calculated)                 │
│  4. postAtEnd concentration                         │
│  5. t½ = 0.693 / Ke                                 │
│  6. CL = Ke × Vd                                    │
│  7. trough = postAtEnd × e^(−Ke × (τ − t_inf))     │
│  8. AUC24 = (Dose × 24) / (CL × τ)                 │
└──────────────────────────┬──────────────────────────┘
                           │
                           ▼
                    Result<TdmResult>
                     ├── metrics: List<ResultMetric>
                     │    [Ke, t½, Vd, CL, trough, AUC24]
                     ├── steps: List<CalculationStep>
                     │    [Step 1–5 with title, detail, equation]
                     ├── warnings: List<String>
                     └── modelNote: String
```

---

## 4. Camera / OCR Flow

```
User taps camera icon (field = "pre" | "post")
       │
       │ vm.startCamera(field)
       ▼
TdmNavigation → route "camera"
       │
CameraScreen
  ├── Permission check (Accompanist)
  │    ├── Granted → CameraX PreviewView
  │    ├── Rationale → explanation screen
  │    └── Denied → settings guidance
  │
  └── User taps shutter
       │ capturePhoto() on main executor (ContextCompat.getMainExecutor)
       │ → saved to cache/camera/LAB_*.jpg
       │ → FileProvider URI created
       │
       │ vm.onImageCaptured(uri)
       ▼
TdmNavigation → route "review"  (camera popped from back stack)
       │
LabReportReviewScreen
  ├── AsyncImage shows captured photo
  ├── LaunchedEffect → vm.runOcr(context, uri)
  │    │ ML Kit TextRecognition (on-device, IO dispatcher)
  │    │ extractCandidates() → filter 0.1–500, sort by clinical range
  │    └── OcrState.Done(candidates)
  │
  ├── User selects candidate OR types manually
  ├── WarningBanner always visible
  └── User taps "Confirm & Use This Value"
       │ vm.confirmOcrValue(field, value)
       │ → update(field, value) injects into form
       │ → camera state cleared
       ▼
Back to CalculatorScreen (value now in field)
```

---

## 5. PDF Export Flow

```
ResultsScreen → "Download / Share Report (PDF)" button
       │
       │ vm.exportPdf(context)
       │ ExportState → Generating
       ▼
IO dispatcher:
  PdfExporter.export(context, result, input)
  ├── Page 1: Navy header, workflow chip, timestamp
  │           Patient summary card (age, weight, sex, creatinine, dose)
  │           PK metrics table (3 columns, AUC24/trough highlighted)
  └── Page 2: Calculation trail (numbered badges + connector lines)
              Clinical warning banners (amber stripe)
              Page numbers + footer
       │
       │ Writes to cache/reports/TDM_Report_*.pdf
       │ FileProvider URI returned
       │ ExportState → Ready(uri)
       ▼
LaunchedEffect in ResultsScreen detects ExportState.Ready
  → Intent.ACTION_SEND (application/pdf)
  → FLAG_GRANT_READ_URI_PERMISSION
  → context.startActivity(createChooser(...))
  → vm.clearExportState()
```

---

## 6. File Structure

```
app/src/main/java/my/edu/aiu/app/tdminsight/
│
├── MainActivity.kt                    Entry point
│
├── data/
│   ├── export/
│   │   └── PdfExporter.kt             PDF generation (Android PdfDocument)
│   └── local/
│       ├── CalculationRecord.kt       Data class for history records
│       └── CalculationRepository.kt   SharedPreferences persistence
│
├── domain/
│   ├── calculator/
│   │   └── VancomycinCalculator.kt    PK engine (pure Kotlin, no Android imports)
│   └── validation/
│       └── TdmValidation.kt           Validation rules (pure Kotlin object)
│
├── model/
│   ├── OcrModels.kt                   OcrCandidate data class
│   ├── Patient.kt                     Patient demographics model
│   ├── TdmInput.kt                    Calculation input model + Workflow enum
│   └── TdmResult.kt                   Result model: metrics, steps, warnings
│
├── ui/
│   ├── components/
│   │   └── TdmComponents.kt           Reusable Composables (30+ components)
│   ├── navigation/
│   │   └── TdmNavigation.kt           NavHost with 7 routes + animations
│   ├── screens/
│   │   ├── SplashScreen.kt            Animated splash
│   │   ├── TdmScreens.kt              Calculator, Results, Explanation screens
│   │   ├── CameraScreen.kt            CameraX live preview + capture
│   │   ├── HistoryScreen.kt           Calculation history list
│   │   └── LabReportReviewScreen.kt   OCR review + confirmation
│   └── theme/
│       └── Theme.kt                   TdmPalette, Typography, Shapes, TdmInsightTheme
│
└── viewmodel/
    └── TdmViewModel.kt                MVVM hub: state, calculation, OCR, PDF export
```
