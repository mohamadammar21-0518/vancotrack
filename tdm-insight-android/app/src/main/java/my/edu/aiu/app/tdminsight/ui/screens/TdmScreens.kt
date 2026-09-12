package my.edu.aiu.app.tdminsight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import my.edu.aiu.app.tdminsight.model.Sex
import my.edu.aiu.app.tdminsight.model.Workflow
import my.edu.aiu.app.tdminsight.ui.components.Disclaimer
import my.edu.aiu.app.tdminsight.ui.components.ErrorBanner
import my.edu.aiu.app.tdminsight.ui.components.GradientHeaderCard
import my.edu.aiu.app.tdminsight.ui.components.InfoBanner
import my.edu.aiu.app.tdminsight.ui.components.LoadingOverlay
import my.edu.aiu.app.tdminsight.ui.components.MetricCard
import my.edu.aiu.app.tdminsight.ui.components.SectionLabel
import my.edu.aiu.app.tdminsight.ui.components.StepBadge
import my.edu.aiu.app.tdminsight.ui.components.StepIndicator
import my.edu.aiu.app.tdminsight.ui.components.TdmField
import my.edu.aiu.app.tdminsight.ui.components.TdmPrimaryButton
import my.edu.aiu.app.tdminsight.ui.components.TdmSecondaryButton
import my.edu.aiu.app.tdminsight.ui.components.WarningBanner
import my.edu.aiu.app.tdminsight.ui.theme.TdmPalette
import my.edu.aiu.app.tdminsight.viewmodel.TdmViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Calculator Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: TdmViewModel = viewModel(),
    onCalculated: () -> Unit,
    onOpenCamera: (String) -> Unit = {},
    onOpenHistory: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val hasErrors = state.errors.isNotEmpty()

    Scaffold(
        containerColor = TdmPalette.PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = TdmPalette.Teal600,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            "VancoTrack",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TdmPalette.Navy800,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Calculation history",
                            tint = TdmPalette.Muted,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TdmPalette.White,
                    scrolledContainerColor = TdmPalette.White,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize(),
            ) {

                // ── Hero header ──────────────────────────────────────────────
                item {
                    GradientHeaderCard(
                        title    = "Vancomycin\nTDM Calculator",
                        subtitle = "Pharmacokinetic dose monitoring",
                        trailing = {
                            Surface(
                                color  = TdmPalette.Teal600.copy(alpha = 0.2f),
                                shape  = CircleShape,
                                modifier = Modifier.size(56.dp),
                            ) {
                                Icon(
                                    Icons.Default.Vaccines,
                                    contentDescription = null,
                                    tint = TdmPalette.Teal400,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                )
                            }
                        },
                    )
                }

                // ── Step progress ────────────────────────────────────────────
                item { StepIndicator(currentStep = 0, totalSteps = 3) }

                // ── Quick-load sample card ───────────────────────────────────
                item {
                    Card(
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TdmPalette.Navy800),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Surface(
                                color = TdmPalette.Teal600,
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    tint   = Color.White,
                                    modifier = Modifier.padding(10.dp).size(20.dp),
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Load Sample Case",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TdmPalette.White,
                                )
                                Text(
                                    "Pre-filled data to explore workflows",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB4C4D8),
                                )
                            }
                            TdmSecondaryButton(
                                    label = "Load",
                                    onClick = { viewModel.loadSample() },
                                    fillWidth = false,
                                    modifier = Modifier.width(80.dp),
                                )
                        }
                    }
                }

                // ── Step 1: Patient ──────────────────────────────────────────
                item {
                    SectionLabel("Step 1 · Patient Information", icon = Icons.Default.Person)
                }
                item {
                    Card(
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TdmPalette.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                TdmField(
                                    label = "Age",
                                    value = state.age,
                                    onValueChange = { viewModel.update("age", it) },
                                    unit  = "years",
                                    modifier = Modifier.weight(1f),
                                    hint  = "e.g. 54",
                                )
                                TdmField(
                                    label = "Weight",
                                    value = state.weight,
                                    onValueChange = { viewModel.update("weight", it) },
                                    unit  = "kg",
                                    modifier = Modifier.weight(1f),
                                    hint  = "e.g. 72",
                                )
                            }

                            // Biological sex chips
                            Column {
                                Text(
                                    "Biological Sex",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TdmPalette.Ink,
                                    modifier = Modifier.padding(bottom = 6.dp),
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Sex.values().forEach { sex ->
                                        FilterChip(
                                            selected = state.sex == sex,
                                            onClick  = { viewModel.setSex(sex) },
                                            label    = {
                                                Text(
                                                    sex.name.lowercase()
                                                        .replaceFirstChar { it.uppercase() },
                                                    fontWeight = FontWeight.Medium,
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = TdmPalette.Teal600,
                                                selectedLabelColor     = Color.White,
                                            ),
                                        )
                                    }
                                }
                            }

                            TdmField(
                                label = "Serum Creatinine",
                                value = state.creatinine,
                                onValueChange = { viewModel.update("creatinine", it) },
                                unit  = "mg/dL",
                                modifier = Modifier.fillMaxWidth(),
                                hint  = "e.g. 0.9",
                            )
                        }
                    }
                }

                // ── Step 2: Dosing ───────────────────────────────────────────
                item {
                    SectionLabel("Step 2 · Medication & Dosing", icon = Icons.Default.MedicalServices)
                }
                item {
                    Card(
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TdmPalette.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                TdmField(
                                    label = "Dose",
                                    value = state.dose,
                                    onValueChange = { viewModel.update("dose", it) },
                                    unit  = "mg",
                                    modifier = Modifier.weight(1f),
                                    hint  = "e.g. 1000",
                                )
                                TdmField(
                                    label = "Interval",
                                    value = state.interval,
                                    onValueChange = { viewModel.update("interval", it) },
                                    unit  = "hr",
                                    modifier = Modifier.weight(1f),
                                    hint  = "e.g. 12",
                                )
                            }
                            TdmField(
                                label = "Infusion Duration",
                                value = state.infusion,
                                onValueChange = { viewModel.update("infusion", it) },
                                unit  = "hr",
                                modifier = Modifier.fillMaxWidth(),
                                hint  = "e.g. 1.5",
                            )

                            // Workflow chips
                            Column {
                                Text(
                                    "Calculation Workflow",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TdmPalette.Ink,
                                    modifier = Modifier.padding(bottom = 6.dp),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Workflow.values().forEach { wf ->
                                        FilterChip(
                                            selected = state.workflow == wf,
                                            onClick  = { viewModel.setWorkflow(wf) },
                                            label    = {
                                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        wf.label,
                                                        fontWeight = FontWeight.SemiBold,
                                                        style = MaterialTheme.typography.labelMedium,
                                                    )
                                                    Text(
                                                        workflowDescription(wf),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = if (state.workflow == wf) Color.White.copy(0.8f)
                                                                else TdmPalette.Muted,
                                                    )
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = TdmPalette.Teal600,
                                                selectedLabelColor     = Color.White,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Step 3: Concentrations ───────────────────────────────────
                item {
                    SectionLabel("Step 3 · Concentration Samples", icon = Icons.Default.Science)
                }
                item {
                    Card(
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TdmPalette.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Camera hint chip
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TdmPalette.InfoBg, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = TdmPalette.InfoBlue,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    "Tap the camera icon to scan a lab report value",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TdmPalette.InfoBlue,
                                )
                            }

                            if (state.workflow == Workflow.PRE || state.workflow == Workflow.PRE_POST) {
                                CameraField(
                                    label = "Pre-Dose Concentration",
                                    value = state.pre,
                                    onValueChange = { viewModel.update("pre", it) },
                                    unit  = "mg/L",
                                    hint  = "Trough level, e.g. 12.4",
                                    onCameraClick = { onOpenCamera("pre") },
                                )
                            }
                            if (state.workflow == Workflow.POST || state.workflow == Workflow.PRE_POST) {
                                CameraField(
                                    label = "Post-Dose Concentration",
                                    value = state.post,
                                    onValueChange = { viewModel.update("post", it) },
                                    unit  = "mg/L",
                                    hint  = "Peak level, e.g. 28.6",
                                    onCameraClick = { onOpenCamera("post") },
                                )
                                TdmField(
                                    label = "Post Sample Time After Infusion End",
                                    value = state.postAfterEnd,
                                    onValueChange = { viewModel.update("postAfterEnd", it) },
                                    unit  = "hr",
                                    modifier = Modifier.fillMaxWidth(),
                                    hint  = "e.g. 1",
                                )
                            }
                            if (state.workflow == Workflow.PRE_POST) {
                                TdmField(
                                    label = "Hours Between Post and Pre Samples",
                                    value = state.postToPre,
                                    onValueChange = { viewModel.update("postToPre", it) },
                                    unit  = "hr",
                                    modifier = Modifier.fillMaxWidth(),
                                    hint  = "e.g. 8",
                                )
                            }
                            if (
                                state.workflow != Workflow.PRE &&
                                state.workflow != Workflow.POST &&
                                state.workflow != Workflow.PRE_POST
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(TdmPalette.PageBg, RoundedCornerShape(8.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "Select a workflow above to see the required fields.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TdmPalette.Muted,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Validation errors ────────────────────────────────────────
                if (hasErrors) {
                    item {
                        SectionLabel("Validation Issues")
                    }
                    items(state.errors) { err ->
                        ErrorBanner(err)
                    }
                }

                // ── CTA + info ───────────────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TdmPrimaryButton(
                            label   = "Validate & Calculate",
                            onClick = {
                                viewModel.calculate(onSuccess = onCalculated)
                            },
                            enabled = !hasErrors && !state.isCalculating,
                            icon    = Icons.Default.Calculate,
                        )
                        InfoBanner("Review all values before calculating. Fields are validated for clinical reasonableness.")
                        Disclaimer()
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Loading overlay on top
            if (state.isCalculating) {
                LoadingOverlay()
            }
        }
    }
}

private fun workflowDescription(wf: Workflow) = when (wf) {
    Workflow.PRE      -> "Uses pre-dose (trough) concentration only"
    Workflow.POST     -> "Uses post-dose (peak) concentration + timing"
    Workflow.PRE_POST -> "Uses both pre and post concentrations"
}

// ─────────────────────────────────────────────────────────────────────────────
// Results Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    vm: TdmViewModel = viewModel(),
    onExplain: () -> Unit,
    onReset: () -> Unit,
) {
    val state   by vm.state.collectAsState()
    val result   = state.result ?: return
    val context  = LocalContext.current
    val exportState = state.exportState

    // Launch Android share sheet as soon as PDF is ready
    LaunchedEffect(exportState) {
        if (exportState is my.edu.aiu.app.tdminsight.viewmodel.ExportState.Ready) {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type  = "application/pdf"
                putExtra(android.content.Intent.EXTRA_STREAM, exportState.uri)
                putExtra(android.content.Intent.EXTRA_SUBJECT, "VancoTrack — Calculation Report")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                android.content.Intent.createChooser(intent, "Share or Save PDF Report")
            )
            vm.clearExportState()
        }
    }

    Scaffold(
        containerColor = TdmPalette.PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Results",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TdmPalette.Navy800,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onReset) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TdmPalette.Navy800,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TdmPalette.White),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize(),
        ) {

            // ── Success hero ─────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(TdmPalette.Success, Color(0xFF0F9A52))
                            ),
                            shape = RoundedCornerShape(20.dp),
                        )
                        .padding(20.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(52.dp),
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                            )
                        }
                        Column {
                            Text(
                                "Calculation Complete",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                result.workflow.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
            }

            // ── PK outputs label ─────────────────────────────────────────────
            item {
                SectionLabel("Pharmacokinetic Outputs", icon = Icons.Default.Science)
            }

            // ── Metric grid: pair up metrics, highlight AUC and Trough ───────
            item {
                val metrics = result.metrics
                val highlightLabels = setOf("Predicted trough", "Estimated AUC24")
                // Build rows of 2
                val rows = metrics.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    rows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            row.forEach { metric ->
                                MetricCard(
                                    label       = metric.label,
                                    value       = metric.value,
                                    unit        = metric.unit,
                                    description = metric.description,
                                    isHighlight = metric.label in highlightLabels,
                                    modifier    = Modifier.weight(1f),
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Explanation CTA ──────────────────────────────────────────────
            item {
                SectionLabel("Understand the Calculation", icon = Icons.Default.Calculate)
            }
            item {
                Card(
                    shape  = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TdmPalette.InfoBg),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            "Step-by-step breakdown of every intermediate value, from Cockcroft-Gault CrCl through to AUC₂₄.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TdmPalette.InfoBlue,
                        )
                        TdmPrimaryButton(
                            label  = "Open Calculation Explanation",
                            onClick = onExplain,
                            icon   = Icons.Default.Science,
                        )
                    }
                }
            }

            // ── Clinical warnings ────────────────────────────────────────────
            if (result.warnings.isNotEmpty()) {
                item {
                    SectionLabel("Clinical Review Notes")
                }
                items(result.warnings) { w -> WarningBanner(w) }
            }

            // ── Footer ───────────────────────────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    // PDF export / share button
                    val isGenerating = exportState is my.edu.aiu.app.tdminsight.viewmodel.ExportState.Generating
                    val isError      = exportState is my.edu.aiu.app.tdminsight.viewmodel.ExportState.Error

                    TdmPrimaryButton(
                        label   = if (isGenerating) "Generating PDF…" else "Download / Share Report (PDF)",
                        onClick = { vm.exportPdf(context) },
                        enabled = !isGenerating,
                        icon    = Icons.Default.Download,
                    )

                    if (isError) {
                        my.edu.aiu.app.tdminsight.ui.components.ErrorBanner(
                            (exportState as my.edu.aiu.app.tdminsight.viewmodel.ExportState.Error).message
                        )
                    }

                    TdmSecondaryButton(
                        label   = "Start New Calculation",
                        onClick = onReset,
                        icon    = Icons.Default.Refresh,
                    )
                    Disclaimer()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Explanation Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplanationScreen(
    vm: TdmViewModel = viewModel(),
    onBack: () -> Unit,
) {
    val result = vm.state.collectAsState().value.result ?: return

    Scaffold(
        containerColor = TdmPalette.PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calculation Explanation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TdmPalette.Navy800,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TdmPalette.Navy800,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TdmPalette.White),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize(),
        ) {

            // ── Workflow context card ────────────────────────────────────────
            item {
                Card(
                    shape  = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TdmPalette.Navy800),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Surface(
                            color = TdmPalette.Teal600,
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp),
                        ) {
                            Icon(
                                Icons.Default.Science,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                            )
                        }
                        Column {
                            Text(
                                result.workflow.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = TdmPalette.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "One-compartment, first-order kinetics",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB4C4D8),
                            )
                        }
                    }
                }
            }

            // ── Flow trail chips ─────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    listOf("Input", "Intermediate", "Parameters", "Result")
                        .forEachIndexed { i, label ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (i == 3) TdmPalette.Teal600 else TdmPalette.PageBg,
                                        shape = RoundedCornerShape(20.dp),
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (i == 3) Color.White else TdmPalette.Muted,
                                )
                            }
                            if (i < 3) {
                                Text(
                                    "→",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TdmPalette.Muted,
                                )
                            }
                        }
                }
            }

            // ── Section label ────────────────────────────────────────────────
            item { SectionLabel("Calculation Trail") }

            // ── Timeline steps ───────────────────────────────────────────────
            items(result.steps.withIndex().toList()) { (index, step) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Left: badge + vertical line
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        StepBadge(number = index + 1)
                        if (index < result.steps.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(24.dp)
                                    .background(TdmPalette.Border),
                            )
                        }
                    }
                    // Right: content card
                    Card(
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TdmPalette.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (index < result.steps.lastIndex) 10.dp else 0.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                step.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = TdmPalette.Navy800,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                step.detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = TdmPalette.Muted,
                                lineHeight = 18.sp,
                            )
                            if (step.equation.isNotBlank()) {
                                Spacer(Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = TdmPalette.PageBg,
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .padding(10.dp),
                                ) {
                                    Text(
                                        step.equation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                        ),
                                        color = TdmPalette.Teal600,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Model note ───────────────────────────────────────────────────
            item { SectionLabel("Model Assumptions") }
            item {
                Card(
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TdmPalette.WarningBg),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Assumptions",
                            style = MaterialTheme.typography.titleSmall,
                            color = TdmPalette.Warning,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            result.modelNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = TdmPalette.Warning,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }

            // ── Footer ───────────────────────────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TdmSecondaryButton("Back to Results", onBack, icon = Icons.AutoMirrored.Filled.ArrowBack)
                    Disclaimer()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CameraField — a TdmField with a teal camera icon button on the trailing end
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CameraField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String = "",
    hint: String = "",
    onCameraClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label row with unit badge (same style as TdmField)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TdmPalette.Ink,
            )
            if (unit.isNotBlank()) {
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(TdmPalette.Teal100, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = TdmPalette.Teal600,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        // Input row: text field + camera button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(hint, style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TdmPalette.Ink),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                ),
                shape = RoundedCornerShape(10.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TdmPalette.Teal600,
                    unfocusedBorderColor = TdmPalette.Border,
                    focusedContainerColor   = TdmPalette.White,
                    unfocusedContainerColor = TdmPalette.White,
                ),
            )
            // Teal camera button
            Surface(
                onClick = onCameraClick,
                shape   = RoundedCornerShape(10.dp),
                color   = TdmPalette.Teal600,
                modifier = Modifier.size(52.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Scan lab report",
                        tint     = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
