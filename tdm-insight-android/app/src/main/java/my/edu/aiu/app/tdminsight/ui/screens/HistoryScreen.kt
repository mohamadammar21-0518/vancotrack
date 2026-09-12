package my.edu.aiu.app.tdminsight.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import my.edu.aiu.app.tdminsight.data.local.CalculationRecord
import my.edu.aiu.app.tdminsight.ui.components.SectionLabel
import my.edu.aiu.app.tdminsight.ui.theme.TdmPalette
import my.edu.aiu.app.tdminsight.viewmodel.TdmViewModel

// ─────────────────────────────────────────────────────────────────────────────
// HistoryScreen — shows up to 20 recent calculations
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    vm: TdmViewModel = viewModel(),
    onBack: () -> Unit,
) {
    val history by vm.history.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    // ── Clear-all confirmation dialog ────────────────────────────────────────
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon  = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = TdmPalette.Error) },
            title = { Text("Clear All History?", fontWeight = FontWeight.Bold) },
            text  = { Text("This will permanently delete all ${history.size} saved calculations. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearHistory()
                    showClearDialog = false
                }) {
                    Text("Clear All", color = TdmPalette.Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = TdmPalette.White,
        )
    }

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
                            Icons.Default.History,
                            contentDescription = null,
                            tint = TdmPalette.Teal600,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            "Calculation History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TdmPalette.Navy800,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TdmPalette.Navy800)
                    }
                },
                actions = {
                    if (history.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                Icons.Default.DeleteForever,
                                contentDescription = "Clear all history",
                                tint = TdmPalette.Error,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TdmPalette.White),
            )
        },
    ) { innerPadding ->

        AnimatedVisibility(
            visible = history.isEmpty(),
            enter   = fadeIn(),
            exit    = fadeOut(),
        ) {
            EmptyHistoryState(modifier = Modifier.fillMaxSize().padding(innerPadding))
        }

        AnimatedVisibility(
            visible = history.isNotEmpty(),
            enter   = fadeIn(),
            exit    = fadeOut(),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top   = innerPadding.calculateTopPadding() + 12.dp,
                    bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    SectionLabel(
                        "${history.size} saved calculation${if (history.size != 1) "s" else ""} · Most recent first"
                    )
                }

                items(history, key = { it.id }) { record ->
                    HistoryCard(
                        record   = record,
                        onDelete = { vm.deleteHistoryRecord(record.id) },
                    )
                }

                item {
                    Text(
                        "Calculations are stored locally on this device. Maximum 20 records kept.",
                        style     = MaterialTheme.typography.labelSmall,
                        color     = TdmPalette.Muted,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Single history card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HistoryCard(
    record: CalculationRecord,
    onDelete: () -> Unit,
) {
    Card(
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = TdmPalette.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row: workflow badge + date + delete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Workflow colour badge
                val (badgeColor, badgeText) = when (record.workflow) {
                    my.edu.aiu.app.tdminsight.model.Workflow.PRE      ->
                        TdmPalette.InfoBlue to "PRE"
                    my.edu.aiu.app.tdminsight.model.Workflow.POST     ->
                        TdmPalette.Teal600  to "POST"
                    my.edu.aiu.app.tdminsight.model.Workflow.PRE_POST ->
                        TdmPalette.Navy800  to "PRE+POST"
                }
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.width(10.dp))
                Text(
                    record.getWorkflowLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    color = TdmPalette.Navy800,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete record",
                        tint = TdmPalette.Muted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Patient & dosing summary chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(
                    "Age ${record.patientAge.toInt()}y",
                    "${record.patientWeight}kg",
                    "${record.dose.toInt()}mg",
                    "Q${record.interval.toInt()}H",
                ).forEach { chip ->
                    Box(
                        modifier = Modifier
                            .background(TdmPalette.PageBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            chip,
                            style = MaterialTheme.typography.labelSmall,
                            color = TdmPalette.Muted,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            // Concentrations row (if available)
            val hasConc = record.preConcentration != null || record.postConcentration != null
            if (hasConc) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    record.preConcentration?.let { pre ->
                        SummaryPill("Pre ${"%1.1f".format(pre)} mg/L", TdmPalette.InfoBg, TdmPalette.InfoBlue)
                    }
                    record.postConcentration?.let { post ->
                        SummaryPill("Post ${"%1.1f".format(post)} mg/L", TdmPalette.Teal100, TdmPalette.Teal600)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Timestamp footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = TdmPalette.Muted,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    record.getFormattedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TdmPalette.Muted,
                )
            }
        }
    }
}

@Composable
private fun SummaryPill(label: String, bg: Color, text: Color) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = text, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(48.dp),
        ) {
            Surface(
                color  = TdmPalette.PageBg,
                shape  = CircleShape,
                modifier = Modifier.size(80.dp),
            ) {
                Icon(
                    Icons.Default.Science,
                    contentDescription = null,
                    tint     = TdmPalette.Border,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                )
            }
            Text(
                "No Calculations Yet",
                style = MaterialTheme.typography.titleMedium,
                color = TdmPalette.Navy800,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                "Completed calculations will appear here automatically. Run your first TDM calculation to get started.",
                style     = MaterialTheme.typography.bodySmall,
                color     = TdmPalette.Muted,
                textAlign = TextAlign.Center,
            )
        }
    }
}
