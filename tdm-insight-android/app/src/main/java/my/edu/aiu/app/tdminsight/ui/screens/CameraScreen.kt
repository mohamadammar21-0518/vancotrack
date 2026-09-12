package my.edu.aiu.app.tdminsight.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import my.edu.aiu.app.tdminsight.ui.theme.TdmPalette
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// CameraScreen
// Flow: Permission check → Live preview → Capture → callback with Uri
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    targetField: String,           // e.g. "pre", "post" — shown in hint label
    onImageCaptured: (Uri) -> Unit, // navigates to review screen with the uri
    onBack: () -> Unit,
) {
    val context      = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Request permission on first composition
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
    }

    when {
        cameraPermission.status.isGranted -> {
            CameraPreview(
                targetField     = targetField,
                onImageCaptured = onImageCaptured,
                onBack          = onBack,
                context         = context,
            )
        }
        cameraPermission.status.shouldShowRationale -> {
            PermissionRationale(
                onRequest = { cameraPermission.launchPermissionRequest() },
                onBack    = onBack,
            )
        }
        else -> {
            PermissionDenied(onBack = onBack)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Live camera preview + capture
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CameraPreview(
    targetField: String,
    onImageCaptured: (Uri) -> Unit,
    onBack: () -> Unit,
    context: Context,
) {
    val lifecycleOwner  = LocalLifecycleOwner.current
    val imageCapture    = remember { ImageCapture.Builder().build() }
    var isCapturing     by remember { mutableStateOf(false) }

    // Flash mode cycles: AUTO → ON → OFF
    var flashMode       by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    val flashIcon       = when (flashMode) {
        ImageCapture.FLASH_MODE_ON  -> Icons.Default.FlashOn
        ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
        else                        -> Icons.Default.FlashAuto
    }

    // Shutter animation
    val shutterScale by animateFloatAsState(
        targetValue    = if (isCapturing) 0.88f else 1f,
        animationSpec  = tween(120),
        label          = "shutter",
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── CameraX PreviewView ──────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture,
                        )
                    } catch (_: Exception) {}
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )

        // ── Corner guide overlay ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(280.dp)
                .align(Alignment.Center)
                .border(2.dp, TdmPalette.Teal600.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
        )

        // ── Top bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text(
                fieldLabel(targetField),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            // Flash toggle
            IconButton(onClick = {
                flashMode = when (flashMode) {
                    ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                    ImageCapture.FLASH_MODE_ON   -> ImageCapture.FLASH_MODE_OFF
                    else                          -> ImageCapture.FLASH_MODE_AUTO
                }
                imageCapture.flashMode = flashMode
            }) {
                Icon(flashIcon, contentDescription = "Flash", tint = Color.White)
            }
        }

        // ── Bottom hint + shutter ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Point at the lab report · Align values inside the frame",
                color     = Color.White.copy(alpha = 0.8f),
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 32.dp),
            )

            // Shutter button
            Surface(
                onClick = {
                    if (!isCapturing) {
                        isCapturing = true
                        capturePhoto(
                            context      = context,
                            imageCapture = imageCapture,
                            onCaptured   = { uri ->
                                isCapturing = false
                                onImageCaptured(uri)
                            },
                            onError = {
                                isCapturing = false   // unlock button on failure
                            },
                        )
                    }
                },
                shape    = CircleShape,
                color    = Color.White,
                modifier = Modifier
                    .size(72.dp)
                    .scale(shutterScale),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Surface(
                        shape = CircleShape,
                        color = if (isCapturing) TdmPalette.Teal600 else Color.White,
                        modifier = Modifier.size(56.dp),
                    ) {}
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Capture",
                        tint     = if (isCapturing) Color.White else TdmPalette.Navy800,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Permission screens
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PermissionRationale(onRequest: () -> Unit, onBack: () -> Unit) {
    PermissionScreen(
        title   = "Camera Access Needed",
        message = "VancoTrack needs camera access to capture a lab report image. The image is processed on-device and never uploaded.",
        actionLabel = "Grant Permission",
        onAction = onRequest,
        onBack   = onBack,
    )
}

@Composable
private fun PermissionDenied(onBack: () -> Unit) {
    PermissionScreen(
        title   = "Camera Permission Denied",
        message = "Camera permission was denied. Please enable it in Settings → Apps → VancoTrack → Permissions.",
        actionLabel = "Go Back",
        onAction = onBack,
        onBack   = onBack,
    )
}

@Composable
private fun PermissionScreen(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TdmPalette.Navy900)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Surface(color = TdmPalette.Teal600, shape = CircleShape) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.padding(20.dp).size(40.dp),
                )
            }
            Text(title, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFB4C4D8), textAlign = TextAlign.Center)
            androidx.compose.material3.Button(
                onClick = onAction,
                shape   = RoundedCornerShape(12.dp),
                colors  = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = TdmPalette.Teal600),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold)
            }
            androidx.compose.material3.TextButton(onClick = onBack) {
                Text("Back", color = Color(0xFFB4C4D8))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onCaptured: (Uri) -> Unit,
    onError: () -> Unit,
) {
    // Create a temp file in the app cache/camera directory
    val cameraDir = File(context.cacheDir, "camera").also { it.mkdirs() }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val photoFile = File(cameraDir, "LAB_$timestamp.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // ⚠ Use main-thread executor so the callback (and any Compose state/nav
    //   changes inside onCaptured) always runs on the main thread.
    val mainExecutor = ContextCompat.getMainExecutor(context)

    imageCapture.takePicture(
        outputOptions,
        mainExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile,
                )
                onCaptured(uri)   // safe — already on main thread
            }
            override fun onError(exception: ImageCaptureException) {
                onError()         // reset isCapturing so button unlocks
            }
        },
    )
}

private fun fieldLabel(field: String) = when (field) {
    "pre"        -> "Capturing Pre-Dose Concentration"
    "post"       -> "Capturing Post-Dose Concentration"
    "creatinine" -> "Capturing Serum Creatinine"
    else         -> "Capturing Lab Report"
}
