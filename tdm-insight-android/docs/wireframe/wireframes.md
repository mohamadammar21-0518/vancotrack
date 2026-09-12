# VancoTrack — UI Wireframes

> ASCII wireframes representing the layout of each screen. Replace with actual design tool exports (Figma, Adobe XD, etc.) before final submission.

---

## Screen 1: Splash Screen

```
┌─────────────────────────────┐
│                             │
│                             │
│                             │
│        ┌─────────┐          │
│        │  [Icon] │          │  ← Teal rounded square icon
│        └─────────┘          │
│                             │
│        VancoTrack          │  ← Large bold white text
│   Vancomycin Therapeutic    │
│     Drug Monitoring         │  ← Subtitle, muted blue
│         ────                │  ← Teal accent line
│                             │
│  ┌───────┐ ┌─────┐ ┌──────┐ │
│  │3 Flow │ │ PK  │ │Valid │ │  ← Feature badges
│  │ works │ │Eng. │ │ated  │ │
│  └───────┘ └─────┘ └──────┘ │
│                             │
│  ┌─────────────────────┐    │
│  │   Get Started  →    │    │  ← Teal primary button
│  └─────────────────────┘    │
│                             │
│   v1.0 · Vancomycin TDM     │  ← Version label
└─────────────────────────────┘
```

---

## Screen 2: Calculator Screen

```
┌─────────────────────────────┐
│ [🏥] VancoTrack     [🕐]  │  ← TopAppBar, history icon
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ Vancomycin             │ │
│ │ TDM Calculator  [💉]  │ │  ← Gradient navy hero card
│ │ Pharmacokinetic…       │ │
│ └─────────────────────────┘ │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━   │  ← Step progress bar
│ ┌─────────────────────────┐ │
│ │ 🔬 Load Sample Case     │ │
│ │ Pre-filled sample…[Load]│ │  ← Navy card
│ └─────────────────────────┘ │
│                             │
│ 👤 STEP 1 · PATIENT INFO    │  ← Section label (teal)
│ ┌─────────────────────────┐ │
│ │ Age [____] Weight [____]│ │
│ │ Sex:  [Male ✓] [Female] │ │
│ │ Creatinine [__________] │ │
│ └─────────────────────────┘ │
│                             │
│ 💊 STEP 2 · DOSING          │
│ ┌─────────────────────────┐ │
│ │ Dose [____] Interval[__]│ │
│ │ Infusion [_____________]│ │
│ │ Workflow: [Pre][Post][++]│ │
│ └─────────────────────────┘ │
│                             │
│ 🔬 STEP 3 · CONCENTRATIONS  │
│ ┌─────────────────────────┐ │
│ │ ℹ Tap camera to scan    │ │
│ │ Pre-dose [________] [📷]│ │
│ │ Post-dose[________] [📷]│ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ [🧮] Validate & Calculate│ │  ← Teal primary button
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

---

## Screen 3: Results Screen

```
┌─────────────────────────────┐
│ [←] Results                 │  ← TopAppBar
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ ✅ Calculation Complete  │ │
│ │ Vancomycin Pre + Post   │ │  ← Green gradient hero
│ └─────────────────────────┘ │
│                             │
│ 🔬 PHARMACOKINETIC OUTPUTS  │
│ ┌───────────┐ ┌───────────┐ │
│ │ Elim. Rate│ │ Half-life │ │
│ │  0.2456   │ │   2.82    │ │  ← PK metric cards (2-col grid)
│ │  hr⁻¹     │ │   hr      │ │
│ └───────────┘ └───────────┘ │
│ ┌───────────┐ ┌───────────┐ │
│ │    Vd     │ │ Clearance │ │
│ │  48.5 L   │ │ 11.9 L/hr │ │
│ └───────────┘ └───────────┘ │
│ ┌───────────┐ ┌───────────┐ │
│ │  Trough   │ │  AUC 24   │ │
│ │  8.3 mg/L │ │252 mg·hr/L│ │  ← Navy highlight cards
│ └───────────┘ └───────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ [🔬] Open Explanation   │ │  ← Info blue card
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ [📥] Download/Share PDF │ │  ← Teal primary button
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ [↺] Start New Calculation│ │  ← Secondary button
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

---

## Screen 4: Explanation Screen

```
┌─────────────────────────────┐
│ [←] Calculation Explanation │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ 🔬 Vancomycin Pre + Post│ │  ← Navy workflow card
│ │ One-compartment kinetics │ │
│ └─────────────────────────┘ │
│ [Input]→[Inter.]→[Param]→[Result] │  ← Flow trail
│                             │
│ CALCULATION TRAIL           │
│ ●─────────────────────────  │
│ │ 1. Renal-function estimate│  ← Step badge + content card
│ │ CrCl ≈ 78.3 mL/min       │
│ │ ┌ CrCl = [(140−Age)×Wt]/ │
│ │ │         (72 × SCr) …   ┘  ← Monospace equation box
│ ●─────────────────────────  │
│ │ 2. Elimination rate       │
│ │ Ke ≈ 0.2456 hr⁻¹, t½=2.82│
│ ●─────────────────────────  │
│ │ 3. Concentration model    │
│ │ Peak ≈ 32.5 mg/L          │
│ ●─────────────────────────  │
│ │ 4. Exposure estimate      │
│ │ AUC₂₄ ≈ 252 mg·hr/L      │
│ ●                           │
│ │ 5. Pre-dose review        │
│ │ 97% of modelled trough    │
│                             │
│ MODEL ASSUMPTIONS           │
│ ┌─────────────────────────┐ │
│ │ ⚠ Simplified one-comp…  │ │  ← Amber warning card
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

---

## Screen 5: History Screen

```
┌─────────────────────────────┐
│ [←] 🕐 Calculation History [🗑]│  ← TopAppBar, delete-all
├─────────────────────────────┤
│ 3 saved calculations        │  ← Section label
│                             │
│ ┌─────────────────────────┐ │
│ │ [PRE+POST] Vanco Pre+Pos│ [✕]│  ← Workflow badge + delete
│ │ [Age 54y][72kg][1000mg][Q12H]│  ← Patient chips
│ │ [Pre 12.4 mg/L][Post 28.6]  │  ← Concentration pills
│ │ 🕐 Jan 15, 2026 14:30   │ │
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ [POST]   Vanco Post     │ [✕]│
│ │ [Age 45y][68kg][750mg][Q8H] │
│ │ [Post 24.1 mg/L]            │
│ │ 🕐 Jan 14, 2026 09:15   │ │
│ └─────────────────────────┘ │
│                             │
│  Stored locally · Max 20    │  ← Footer note
└─────────────────────────────┘
```

---

## Screen 6: Camera Screen

```
┌─────────────────────────────┐
│[←] Capturing Pre-Dose  [⚡] │  ← Back + flash toggle
│                             │
│  ┌───────────────────────┐  │
│  │                       │  │
│  │   CAMERA PREVIEW      │  │  ← Live CameraX preview
│  │                       │  │
│  │   ┌─────────────┐     │  │
│  │   │             │     │  │  ← Teal guide frame
│  │   │   ALIGN     │     │  │
│  │   │   VALUES    │     │  │
│  │   └─────────────┘     │  │
│  │                       │  │
│  └───────────────────────┘  │
│                             │
│  Point at lab report ·      │
│  Align values in frame      │
│                             │
│         ┌───────┐           │
│         │  ( ) │           │  ← Shutter button
│         └───────┘           │
└─────────────────────────────┘
```

---

## Screen 7: Lab Report Review Screen

```
┌─────────────────────────────┐
│ [←] Review Lab Report       │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ 📋 Filling in: Pre-Dose │ │
│ │ Always verify before…   │ │  ← Navy context card
│ └─────────────────────────┘ │
│                             │
│ CAPTURED IMAGE              │
│ ┌─────────────────────────┐ │
│ │                         │ │
│ │   [Captured Photo]      │ │  ← Coil AsyncImage
│ │                         │ │
│ └─────────────────────────┘ │
│                             │
│ 🔍 DETECTED VALUES          │
│ ℹ 3 values found. Tap to select│
│ ┌─────────────────────────┐ │
│ │ ○  12.4       mg/L      │ │  ← Candidate rows
│ │    "Vanc trough 12.4"   │ │
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ ●  12.4       mg/L      │ │  ← Selected (teal border)
│ │    "Vanc trough 12.4"   │ │
│ └─────────────────────────┘ │
│                             │
│ CONFIRM OR EDIT VALUE       │
│ ┌─────────────────────────┐ │
│ │ Value: [12.4___________]│ │
│ │ ⚠ Always verify before  │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ [✓] Confirm & Use Value │ │  ← Teal primary button
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ [📋] Retake Photo       │ │  ← Secondary button
│ └─────────────────────────┘ │
└─────────────────────────────┘
```
