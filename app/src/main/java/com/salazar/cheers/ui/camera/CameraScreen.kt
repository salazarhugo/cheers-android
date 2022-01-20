package com.salazar.cheers.ui.camera

import android.Manifest
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.salazar.cheers.util.Utils.createFile
import com.salazar.cheers.util.Utils.getOutputDirectory
import com.salazar.cheers.util.Utils.getOutputFileOptions
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class CameraUIAction {
    object OnSettingsClick : CameraUIAction()
    object OnCloseClick : CameraUIAction()
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}

@Composable
fun CameraScreen(
    uiState: CameraUiState,
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier,
    onCameraUIAction: (CameraUIAction) -> Unit,
    onPostClicked: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        CameraPermission {
            CameraPreview(
                imageCapture = imageCapture,
                lensFacing = uiState.lensFacing,
                onCameraUIAction = onCameraUIAction,
                uiState = uiState,
            )
            CameraFooter(
                imageTaken = uiState.imageUri != null,
                cameraUIAction = onCameraUIAction,
                onPostClicked = onPostClicked,
            )
        }
    }
}

@Composable
fun CameraFooter(
    imageTaken: Boolean,
    cameraUIAction: (CameraUIAction) -> Unit,
    onPostClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageTaken)
            CameraFooterSendTo(onPostClicked = onPostClicked)
        else
            CameraFooterIdle(cameraUIAction)
    }
}

@Composable
fun CameraFooterSendTo(
    onPostClicked: () -> Unit,
) {
    FilledTonalButton(
        modifier = Modifier,
        onClick = onPostClicked,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Post")
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Filled.ChevronRight, null)
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

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@Composable
fun CameraControls(
    cameraUIAction: (CameraUIAction) -> Unit,
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
fun CameraPreview(
    uiState: CameraUiState,
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

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9 / 16f)
            .clip(RoundedCornerShape(16.dp)),
    ) {
        if (uiState.imageUri != null)
            Image(
                painter = rememberImagePainter(uiState.imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 16f)
            )
        else {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9 / 16f)
            )
            CameraControls(onCameraUIAction)
        }
    }
}

@Composable
private fun CameraPermission(
    navigateToSettingsScreen: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    // Track if the user doesn't want to see the rationale any more.
    val doNotShowRationale = rememberSaveable { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            if (doNotShowRationale.value) {
                Text("Feature not available")
            } else {
                Column {
                    Text("The camera is important for this app. Please grant the permission.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Ok")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { doNotShowRationale.value = true }) {
                            Text("No")
                        }
                    }
                }
            }
        },
        permissionNotAvailableContent = {
            Column {
                Text(
                    "Camera permission denied. See this FAQ with information about why we " +
                            "need this permission. Please, grant us access on the Settings screen."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = navigateToSettingsScreen) {
                    Text("Open Settings")
                }
            }
        }
    ) {
        content()
    }
}

fun ImageCapture.takePicture(
    context: Context,
    lensFacing: Int,
    onImageCaptured: (Uri, Boolean) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val outputDirectory = context.getOutputDirectory()
    // Create output file to hold the image

    val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
    val PHOTO_EXTENSION = ".jpg"

    val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
    val outputFileOptions = getOutputFileOptions(lensFacing, photoFile)

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

