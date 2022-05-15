package com.salazar.cheers.ui.main.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoAlbum
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.components.utils.Permission
import com.salazar.cheers.util.Utils.createFile
import com.salazar.cheers.util.Utils.getOutputDirectory
import com.salazar.cheers.util.Utils.getOutputFileOptions
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class CameraUIAction {
    object OnAddContent : CameraUIAction()
    object OnSettingsClick : CameraUIAction()
    object OnCloseClick : CameraUIAction()
    object OnBackClick : CameraUIAction()
    object OnCameraClick : CameraUIAction()
    object OnFlashClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSendClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}

@Composable
fun CameraScreen(
    uiState: CameraUiState,
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier,
    onCameraUIAction: (CameraUIAction) -> Unit,
    onPostClicked: () -> Unit,
    onStoryClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.Black,
        bottomBar = {
            CameraFooter(
                imageTaken = uiState.imageUri != null,
                cameraUIAction = onCameraUIAction,
                onPostClicked = onPostClicked,
                onStoryClick = onStoryClick,
            )
        }
    ) {
        Permission(Manifest.permission.CAMERA) {
            CameraPreview(
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
fun CameraFooter(
    imageTaken: Boolean,
    cameraUIAction: (CameraUIAction) -> Unit,
    onPostClicked: () -> Unit,
    onStoryClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageTaken)
            CameraFooterSendTo(
                onPostClicked = onPostClicked,
                onStoryClick = onStoryClick,
            )
        else
            CameraFooterIdle(cameraUIAction)
    }
}

@Composable
fun CameraFooterSendTo(
    onPostClicked: () -> Unit,
    onStoryClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = onStoryClick,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.AccountCircle, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text("Your Story")
            }
        }
        Spacer(Modifier.width(8.dp))
        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = onPostClicked,
        ) {
            Text("Post")
        }
    }
}

@Composable
fun CameraFooterIdle(
    cameraUIAction: (CameraUIAction) -> Unit,
) {
    IconButton(
        onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) },
    ) {
        Icon(
            Icons.Default.PhotoAlbum,
            contentDescription = null,
            tint = Color.White,
        )
    }
    Text(
        text = "POST",
        textAlign = TextAlign.Center,
        color = Color.White,
    )
    IconButton(
        onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) },
    ) {
        Icon(
            Icons.Outlined.FlipCameraAndroid,
            contentDescription = null,
            tint = Color.White,
        )
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@Composable
fun ImageControls(
    cameraUIAction: (CameraUIAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        IconButton(
            onClick = { cameraUIAction(CameraUIAction.OnBackClick) },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                Icons.Outlined.ChevronLeft,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        IconButton(
            onClick = { cameraUIAction(CameraUIAction.OnAddContent) },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Outlined.StickyNote2,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun Controls(
    cameraUIAction: (CameraUIAction) -> Unit,
    hasImage: Boolean,
    flashMode: Int,
) {
    when (hasImage) {
        true -> ImageControls(cameraUIAction = cameraUIAction)
        false -> CameraControls(
            cameraUIAction = cameraUIAction,
            flashMode = flashMode,
        )
    }
}

@Composable
fun CameraControls(
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

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraPreview(
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
            .fillMaxWidth()
            .aspectRatio(9 / 16f)
            .clip(RoundedCornerShape(22.dp)),
    ) {
        if (imageUri != null)
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 16f)
            )
        else
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 16f)
            )
        Controls(
            cameraUIAction = onCameraUIAction,
            hasImage = imageUri != null,
            flashMode = flashMode,
        )
    }
}

fun ImageCapture.takePicture(
    context: Context,
    lensFacing: Int,
    flashMode: Int = ImageCapture.FLASH_MODE_AUTO,
    onImageCaptured: (Uri, Boolean) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val outputDirectory = context.getOutputDirectory()
    // Create output file to hold the image

    val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
    val PHOTO_EXTENSION = ".jpg"

    val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
    val outputFileOptions = getOutputFileOptions(lensFacing, photoFile)

    setFlashMode(flashMode)

    this.takePicture(
        outputFileOptions,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(savedUri.toFile().extension)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(savedUri.toFile().absolutePath),
                    arrayOf(mimeType)
                ) { _, uri ->

                }
                onImageCaptured(savedUri, false)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        })
}

