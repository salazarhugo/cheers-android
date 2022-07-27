package com.salazar.cheers.ui.main.camera

import android.Manifest
import android.net.Uri
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.compose.utils.Permission

@Composable
fun ChatCameraScreen(
    uiState: ChatCameraUiState,
    imageCapture: ImageCapture,
    onCameraUIAction: (CameraUIAction) -> Unit,
) {
    val roomName = uiState.room?.name ?: ""

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            if (uiState.isImageTaken)
                ChatCameraFooter(
                    cameraUIAction = onCameraUIAction,
                    name = roomName,
                )
        }
    ) {
        Permission(Manifest.permission.CAMERA) {
            ChatCameraPreview(
                imageCapture = imageCapture,
                lensFacing = uiState.lensFacing,
                onCameraUIAction = onCameraUIAction,
                flashMode = uiState.flashMode,
                imageUri = uiState.imageUri,
            )
        }
    }
}

@Composable
fun ChatCameraPreview(
    imageUri: Uri?,
    flashMode: Int,
    imageCapture: ImageCapture,
    lensFacing: Int,
    onCameraUIAction: (CameraUIAction) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()


    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lensFacing, imageUri, flashMode) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0F
                val delta = detector.scaleFactor
                camera.cameraControl.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(context, listener)
        previewView.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    val factory = previewView.meteringPointFactory
                    val point = factory.createPoint(motionEvent.x, motionEvent.y)
                    val action = FocusMeteringAction.Builder(point).build()
                    camera.cameraControl.startFocusAndMetering(action)
                    true
                }
                else -> false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
            .clip(RoundedCornerShape(22.dp)),
    ) {
        if (imageUri != null)
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(9 / 16f)
            )
        else
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
            )
        ChatCameraControls(
            cameraUIAction = onCameraUIAction,
            flashMode = flashMode,
        )
    }
}

@Composable
fun ChatCameraControls(
    cameraUIAction: (CameraUIAction) -> Unit,
    flashMode: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        IconButton(
            onClick = { cameraUIAction(CameraUIAction.OnSettingsClick) },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        IconButton(
            onClick = { cameraUIAction(CameraUIAction.OnFlashClick) },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            val icon = when (flashMode) {
                ImageCapture.FLASH_MODE_AUTO -> Icons.Outlined.FlashAuto
                ImageCapture.FLASH_MODE_ON -> Icons.Outlined.FlashOn
                else -> Icons.Outlined.FlashOff
            }

            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        IconButton(
            onClick = { cameraUIAction(CameraUIAction.OnCloseClick) },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 0.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .clip(CircleShape)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape)
                .padding(7.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable {
                    cameraUIAction(CameraUIAction.OnCameraClick)
                }
        ) {}
    }
}

@Composable
fun ChatCameraFooter(
    name: String,
    cameraUIAction: (CameraUIAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            Text(
                text = "Tap to add friends!",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
        }
        IconButton(
            onClick = { cameraUIAction(CameraUIAction.OnSendClick) },
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

