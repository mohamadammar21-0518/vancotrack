package my.edu.aiu.app.tdminsight.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdminsight.ui.theme.TdmPalette

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {

    // ── Entrance animations ──────────────────────────────────────────────────
    val iconScale  = remember { Animatable(0.5f) }
    val bodyAlpha  = remember { Animatable(0f) }
    val badgeAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing),
        )
        bodyAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, delayMillis = 200),
        )
        badgeAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, delayMillis = 500),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to TdmPalette.Navy900,
                        0.55f to TdmPalette.Navy800,
                        1.0f to Color(0xFF0A2540),
                    )
                )
            ),
    ) {

        // ── Background decorative circles ────────────────────────────────────
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .background(
                    color = TdmPalette.Teal600.copy(alpha = 0.07f),
                    shape = CircleShape,
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .background(
                    color = TdmPalette.Teal600.copy(alpha = 0.05f),
                    shape = CircleShape,
                )
        )

        // ── Main content ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // App icon
            Surface(
                color    = TdmPalette.Teal600,
                shape    = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .size(100.dp)
                    .scale(iconScale.value),
            ) {
                Icon(
                    Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            // App name + tagline
            Column(
                modifier = Modifier
                    .alpha(bodyAlpha.value)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "VancoTrack",
                    style      = MaterialTheme.typography.displaySmall,
                    color      = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign  = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Vancomycin Therapeutic\nDrug Monitoring",
                    style     = MaterialTheme.typography.bodyLarge,
                    color     = Color(0xFFB4C4D8),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                )
                Spacer(Modifier.height(6.dp))
                // Teal accent line
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(3.dp)
                        .background(TdmPalette.Teal600, RoundedCornerShape(2.dp))
                )
            }

            Spacer(Modifier.height(48.dp))

            // Feature badges row
            Row(
                modifier = Modifier
                    .alpha(badgeAlpha.value)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FeatureBadge(
                    icon    = Icons.Default.Vaccines,
                    label   = "3 Workflows",
                    modifier = Modifier.weight(1f),
                )
                FeatureBadge(
                    icon    = Icons.Default.Science,
                    label   = "PK Engine",
                    modifier = Modifier.weight(1f),
                )
                FeatureBadge(
                    icon    = Icons.Default.LocalHospital,
                    label   = "Validated",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(56.dp))

            // CTA button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .alpha(badgeAlpha.value)
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TdmPalette.Teal600,
                    contentColor   = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp),
            ) {
                Text(
                    "Get Started",
                    fontWeight = FontWeight.Bold,
                    style      = MaterialTheme.typography.titleSmall,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // ── Bottom version badge ──────────────────────────────────────────────
        Text(
            "v1.0 · Vancomycin TDM",
            style    = MaterialTheme.typography.labelSmall,
            color    = Color(0xFF50627C),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        )
    }
}

// ── Small feature badge ───────────────────────────────────────────────────────
@Composable
private fun FeatureBadge(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = TdmPalette.Teal400,
            modifier = Modifier.size(24.dp),
        )
        Text(
            label,
            style      = MaterialTheme.typography.labelSmall,
            color      = Color(0xFFB4C4D8),
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center,
        )
    }
}
