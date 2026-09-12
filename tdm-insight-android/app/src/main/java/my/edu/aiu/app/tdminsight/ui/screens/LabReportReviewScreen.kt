package my.edu.aiu.app.tdminsight.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import my.edu.aiu.app.tdminsight.ui.components.InfoBanner
import my.edu.aiu.app.tdminsight.ui.components.SectionLabel
import my.edu.aiu.app.tdminsight.ui.components.TdmPrimaryButton
import my.edu.aiu.app.tdminsight.ui.components.TdmSecondaryButton
import my.edu.aiu.app.tdminsight.ui.components.WarningBanner
import my.edu.aiu.app.tdminsight.ui.theme.TdmPalette
import my.edu.aiu.app.tdminsight.viewmodel.OcrState
import my.edu.aiu.app.tdminsight.viewmodel.TdmViewModel

// ─────────────────────────────────────────────────────────────────────────────
// LabReportReviewScreen
// Shows the captured image + OCR-extracted numeric candidates.
// User selects one (or edits manually) then confirms → value goes into field.
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabReportReviewScreen(
    vm: TdmViewModel = viewModel(),
    onConfirmed: () -> Unit,   // navigate back to calculator
    onRetake: () -> Unit,      // navigate back to camera
    onBack: () -> Unit,
) {
    val state    by vm.state.collectAsState()
    val ocrState  = state.ocrState
    val imageUri  = state.capturedImageUri
    val target    = state.cameraTargetField

    // Local manual-entry value (pre-seeded from selection)
    var manualValue  by remember { mutableStateOf(state.selectedOcrValue) }
    var selectedIdx  by remember { mutableStateOf(-1) }

    // Trigger OCR when screen opens with a new image
    val context = LocalContext.current
    LaunchedEffect(imageUri) {
        if (imageUri != null && ocrState !is OcrState.Done) {
            vm.runOcr(context, imageUri)
        }
    }

    Scaffold(
        containerColor = TdmPalette.PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Review Lab Report",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TdmPalette.Navy800,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TdmPalette.Navy800)
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
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize(),
        ) {

            // ── Target field banner ──────────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TdmPalette.Navy800),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            Icons.Default.ContentPaste,
                            contentDescription = null,
                            tint = TdmPalette.Teal400,
                            modifier = Modifier.size(22.dp),
                        )
                        Column {
                            Text(
                                "Filling in: ${fieldDisplayName(target)}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TdmPalette.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                "Select the matching value below · Always verify before confirming",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB4C4D8),
                            )
                        }
                    }
                }
            }

            // ── Captured image ───────────────────────────────────────────────
            item {
                SectionLabel("Captured Image")
            }
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TdmPalette.Navy900),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Captured lab report",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Text("No image captured", color = Color(0xFFB4C4D8), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // ── OCR results ──────────────────────────────────────────────────
            item { SectionLabel("Detected Values", icon = Icons.Default.Search) }

            when (ocrState) {
                is OcrState.Idle, is OcrState.Running -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TdmPalette.White, RoundedCornerShape(12.dp))
                                .padding(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                CircularProgressIndicator(
                                    color = TdmPalette.Teal600,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(36.dp),
                                )
                                Text(
                                    "Scanning for numeric values…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TdmPalette.Muted,
                                )
                            }
                        }
                    }
                }

                is OcrState.Done -> {
                    val candidates = (ocrState as OcrState.Done).candidates
                    if (candidates.isEmpty()) {
                        item {
                            WarningBanner("No numeric values were detected. Enter the value manually below.")
                        }
                    } else {
                        item {
                            InfoBanner("${candidates.size} numeric value(s) found. Tap to select the correct one.")
                        }
                        items(candidates.size) { i ->
                            val cand = candidates[i]
                            val isSelected = selectedIdx == i
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TdmPalette.Teal100 else TdmPalette.White,
                                ),
                                elevation = CardDefaults.cardElevation(if (isSelected) 2.dp else 1.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) TdmPalette.Teal600 else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .clickable {
                                        selectedIdx = i
                                        manualValue = cand.value
                                        vm.setSelectedOcrValue(cand.value)
                                    },
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Icon(
                                        if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) TdmPalette.Teal600 else TdmPalette.Muted,
                                        modifier = Modifier.size(22.dp),
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            cand.value,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) TdmPalette.Navy800 else TdmPalette.Ink,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 20.sp,
                                        )
                                        if (cand.context.isNotBlank()) {
                                            Text(
                                                "\"${cand.context}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TdmPalette.Muted,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                    if (cand.unit.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .background(TdmPalette.Teal100, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                        ) {
                                            Text(
                                                cand.unit,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TdmPalette.Teal600,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is OcrState.Error -> {
                    item {
                        WarningBanner("OCR scan failed: ${(ocrState as OcrState.Error).message}. Enter the value manually below.")
                    }
                }
            }

            // ── Manual entry / edit ──────────────────────────────────────────
            item { SectionLabel("Confirm or Edit Value") }
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TdmPalette.White),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            "Value for ${fieldDisplayName(target)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TdmPalette.Ink,
                        )
                        OutlinedTextField(
                            value = manualValue,
                            onValueChange = {
                                manualValue = it
                                selectedIdx = -1          // deselect OCR pick on manual edit
                                vm.setSelectedOcrValue(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter value manually if needed") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TdmPalette.Teal600,
                                unfocusedBorderColor = TdmPalette.Border,
                            ),
                        )
                        WarningBanner("Always verify this value against the original lab report before confirming.")
                    }
                }
            }

            // ── Action buttons ───────────────────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TdmPrimaryButton(
                        label = "Confirm & Use This Value",
                        onClick = {
                            vm.confirmOcrValue(target, manualValue)
                            onConfirmed()
                        },
                        enabled = manualValue.isNotBlank(),
                        icon = Icons.Default.CheckCircle,
                    )
                    TdmSecondaryButton(
                        label = "Retake Photo",
                        onClick = onRetake,
                        icon = Icons.Default.ContentPaste,
                    )
                }
            }
        }
    }
}

private fun fieldDisplayName(field: String) = when (field) {
    "pre"        -> "Pre-Dose Concentration (mg/L)"
    "post"       -> "Post-Dose Concentration (mg/L)"
    "creatinine" -> "Serum Creatinine (mg/dL)"
    else         -> field
}
