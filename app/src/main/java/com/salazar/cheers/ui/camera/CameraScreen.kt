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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.salazar.cheers.CheersNavigationActions
import com.salazar.cheers.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}

@Composable
fun CameraScreen(
    uiState: CameraUiState,
    modifier: Modifier = Modifier,
    navActions: CheersNavigationActions,
    onTakePhoto: (Uri) -> Unit,
) {
    CameraPermission { }
    Column {
        if (uiState.imageUri != null) {
            Image(painter = rememberImagePainter(uiState.imageUri), contentDescription = null)
        } else
            Box(contentAlignment = Alignment.BottomCenter) {
                val context = LocalContext.current
                var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

                val imageCapture: ImageCapture = remember {
                    ImageCapture.Builder().build()
                }

                CameraPreview(
                    imageCapture = imageCapture,
                    lensFacing = lensFacing
                ) { cameraUIAction ->
                    when (cameraUIAction) {
                        is CameraUIAction.OnCameraClick -> {
                            imageCapture.takePicture(context, lensFacing, { uri, fromGallery ->
                                onTakePhoto(uri)
                            }, {})
                        }
                        is CameraUIAction.OnSwitchCameraClick -> {
                            lensFacing =
                                if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                        }
                        is CameraUIAction.OnGalleryViewClick -> {}
                    }
                }
            }
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
fun CameraControls(cameraUIAction: (CameraUIAction) -> Unit) {
    IconButton(
        onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) },
    ) {
        Icon(
            Icons.Outlined.FlipCameraAndroid,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.White
        )
    }
    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        shadowElevation = 0.dp,
        modifier = Modifier
            .padding(bottom = 26.dp)
            .clip(CircleShape)
            .size(90.dp)
            .border(4.dp, Color.White, CircleShape)
            .clickable {
                cameraUIAction(CameraUIAction.OnCameraClick)
            }
    ) {}
}

@Composable
fun CameraPreview(
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    cameraUIAction: (CameraUIAction) -> Unit
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
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            CameraControls(cameraUIAction)
        }
    }
}

@Composable
private fun CameraPermission(
    navigateToSettingsScreen: () -> Unit
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
                            Text("Ok!")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { doNotShowRationale.value = true }) {
                            Text("Nope")
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
//            Text("Camera permission Granted")
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

private fun getOutputFileOptions(
    lensFacing: Int,
    photoFile: File
): ImageCapture.OutputFileOptions {

    // Setup image capture metadata
    val metadata = ImageCapture.Metadata().apply {
        // Mirror image when using the front camera
        isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
    }
    // Create output options object which contains file + metadata

    return ImageCapture.OutputFileOptions.Builder(photoFile)
        .setMetadata(metadata)
        .build()
}

private fun createFile(baseFolder: File, format: String, extension: String) =
    File(
        baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension
    )


private fun Context.getOutputDirectory(): File {
    val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
        File(it, this.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else this.filesDir
}