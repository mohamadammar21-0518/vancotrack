package my.edu.aiu.app.tdminsight.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdminsight.ui.theme.TdmPalette

// ── Convenience aliases ──────────────────────────────────────────────────────
object TdmColors {
    val Navy      = TdmPalette.Navy800
    val Ink       = TdmPalette.Ink
    val Muted     = TdmPalette.Muted
    val Teal      = TdmPalette.Teal600
    val Page      = TdmPalette.PageBg
    val Error     = TdmPalette.Error
    val ErrorLight = TdmPalette.ErrorBg
    val Warning   = TdmPalette.Warning
    val WarningLight = TdmPalette.WarningBg
    val Success   = TdmPalette.Success
    val SuccessLight = TdmPalette.SuccessBg
    val Info      = TdmPalette.InfoBlue
    val InfoLight = TdmPalette.InfoBg
}

// ── Input field ──────────────────────────────────────────────────────────────
@Composable
fun TdmField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String = "",
    modifier: Modifier = Modifier,
    hint: String = "",
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TdmColors.Ink,
            )
            if (unit.isNotBlank()) {
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = TdmPalette.Teal100,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
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
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { if (hint.isNotBlank()) Text(hint, style = MaterialTheme.typography.bodyMedium) },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TdmColors.Ink),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = TdmPalette.Teal600,
                unfocusedBorderColor = TdmPalette.Border,
                errorBorderColor     = TdmPalette.Error,
                focusedContainerColor   = TdmPalette.White,
                unfocusedContainerColor = TdmPalette.White,
            ),
        )
    }
}

// ── Primary button ───────────────────────────────────────────────────────────
@Composable
fun TdmPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    val containerColor by animateColorAsState(
        targetValue = if (enabled) TdmPalette.Teal600 else TdmPalette.Muted,
        animationSpec = tween(200), label = "btn"
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor         = containerColor,
            disabledContainerColor = TdmPalette.Border,
            disabledContentColor   = TdmPalette.Muted,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp,
        ),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 0.3.sp,
        )
    }
}

// ── Secondary / outline button ───────────────────────────────────────────────
@Composable
fun TdmSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    fillWidth: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = if (fillWidth) modifier.fillMaxWidth().height(52.dp) else modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TdmPalette.Navy800,
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TdmPalette.Navy800),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

// ── Banners ──────────────────────────────────────────────────────────────────
@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    BannerCard(
        message   = message,
        icon      = Icons.Default.Error,
        iconTint  = TdmColors.Error,
        bg        = TdmColors.ErrorLight,
        textColor = TdmColors.Error,
        modifier  = modifier,
    )
}

@Composable
fun WarningBanner(message: String, modifier: Modifier = Modifier) {
    BannerCard(
        message   = message,
        icon      = Icons.Default.Warning,
        iconTint  = TdmColors.Warning,
        bg        = TdmColors.WarningLight,
        textColor = TdmColors.Warning,
        modifier  = modifier,
    )
}

@Composable
fun InfoBanner(message: String, modifier: Modifier = Modifier) {
    BannerCard(
        message   = message,
        icon      = Icons.Default.Info,
        iconTint  = TdmColors.Info,
        bg        = TdmColors.InfoLight,
        textColor = TdmColors.Info,
        modifier  = modifier,
    )
}

@Composable
fun SuccessBanner(message: String, modifier: Modifier = Modifier) {
    BannerCard(
        message   = message,
        icon      = Icons.Default.CheckCircle,
        iconTint  = TdmColors.Success,
        bg        = TdmColors.SuccessLight,
        textColor = TdmColors.Success,
        modifier  = modifier,
    )
}

@Composable
private fun BannerCard(
    message: String,
    icon: ImageVector,
    iconTint: Color,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = bg),
        shape    = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp).padding(top = 1.dp))
            Text(message, color = textColor, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = TdmPalette.Teal600, modifier = Modifier.size(16.dp))
        }
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TdmPalette.Teal600,
            letterSpacing = 1.sp,
        )
    }
}

// ── Legacy alias so existing usages of SectionDivider still compile ──────────
@Composable
fun SectionDivider(title: String = "", modifier: Modifier = Modifier) {
    SectionLabel(title = title, modifier = modifier)
}

// ── Step progress bar ─────────────────────────────────────────────────────────
@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val active = index <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .background(
                        color = if (active) TdmPalette.Teal600 else TdmPalette.Border,
                        shape = RoundedCornerShape(3.dp),
                    )
            )
        }
    }
}

// ── Timeline step badge ───────────────────────────────────────────────────────
@Composable
fun StepBadge(number: Int, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(32.dp)
            .background(TdmPalette.Teal600, CircleShape),
    ) {
        Text(
            "$number",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

// ── Metric card (results screen) ──────────────────────────────────────────────
@Composable
fun MetricCard(
    label: String,
    value: String,
    unit: String,
    description: String = "",
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isHighlight) TdmPalette.Navy800 else TdmPalette.White
    val labelColor = if (isHighlight) TdmPalette.Teal400 else TdmPalette.Muted
    val valueColor = if (isHighlight) TdmPalette.White else TdmPalette.Ink
    val unitColor  = if (isHighlight) Color(0xFFB4C4D8) else TdmPalette.Muted
    val descColor  = if (isHighlight) Color(0xFFB4C4D8) else TdmPalette.Muted

    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlight) 4.dp else 1.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp,
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = valueColor,
                    fontSize = 20.sp,
                )
                Text(
                    unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = unitColor,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            if (description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = descColor,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

// ── Disclaimer ────────────────────────────────────────────────────────────────
@Composable
fun Disclaimer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TdmPalette.Border, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            tint = TdmPalette.Muted,
            modifier = Modifier.size(14.dp).padding(top = 1.dp),
        )
        Text(
            "For clinical reference only. Do not use for prescribing or treatment decisions.",
            style = MaterialTheme.typography.labelSmall,
            color = TdmPalette.Muted,
            modifier = Modifier.weight(1f),
        )
    }
}

// ── Loading overlay ───────────────────────────────────────────────────────────
@Composable
fun LoadingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TdmPalette.White),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator(
                    color = TdmPalette.Teal600,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(44.dp),
                )
                Text(
                    "Calculating…",
                    style = MaterialTheme.typography.titleSmall,
                    color = TdmPalette.Navy800,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ── Gradient header card (used on each screen top) ────────────────────────────
@Composable
fun GradientHeaderCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(TdmPalette.Navy900, TdmPalette.Navy700)
                ),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TdmPalette.White,
                    fontWeight = FontWeight.Black,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB4C4D8),
                )
            }
            trailing?.invoke()
        }
    }
}
