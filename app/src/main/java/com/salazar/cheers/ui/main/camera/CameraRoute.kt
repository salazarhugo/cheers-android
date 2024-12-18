package com.salazar.cheers.ui.main.camera

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.FunctionalityNotAvailablePanel
import kotlinx.coroutines.launch

@OptIn(ExperimentalZeroShutterLag::class)
@Composable
fun CameraRoute(
    cameraViewModel: CameraViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by cameraViewModel.uiState.collectAsStateWithLifecycle()
    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        .build()
    val imageCapture = remember {
        ImageCapture.Builder()
            .setResolutionSelector(resolutionSelector)
            .setJpegQuality(75)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG)
            .build()
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            cameraViewModel.setImageUri(it)
        }

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("")
                FunctionalityNotAvailablePanel()
            }
        },
        sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        sheetBackgroundColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color(
            0x55FFFFFF
        ),
        sheetElevation = 0.dp,
        scrimColor = Color.Transparent,
    ) {
        CameraScreen(
            uiState = uiState,
            imageCapture = imageCapture,
            onStoryClick = {
                cameraViewModel.uploadStory()
//                navActions.navigateToHome()
            },
            onPostClicked = {
//                if (uiState.imageUri != null)
//                    navActions.navigateToCreatePostSheetWithPhotoUri(uiState.imageUri.toString())
            },
            onCameraUIAction = { cameraUIAction ->
                when (cameraUIAction) {
                    is CameraUIAction.OnSwitchCameraClick ->
                        cameraViewModel.onSwitchCameraClicked()

                    is CameraUIAction.OnCameraClick -> {
                        imageCapture.takePicture(
                            context = context,
                            lensFacing = uiState.lensFacing,
                            flashMode = uiState.flashMode,
                            onImageCaptured = { uri, fromGallery ->
                                cameraViewModel.setImageUri(uri)
                            },
                            onError = {},
                        )
                    }

                    is CameraUIAction.OnGalleryViewClick -> {
                        launcher.launch("image/* video/*")
                    }

                    is CameraUIAction.OnCloseClick -> navigateBack()
                    is CameraUIAction.OnBackClick -> {
                        cameraViewModel.setImageUri(null)
                    }

                    is CameraUIAction.OnFlashClick -> {
                        cameraViewModel.onSwitchFlash()
                    }

                    is CameraUIAction.OnAddContent -> {
                        scope.launch {
                            sheetState.show()
                        }
                    }

                    else -> {}
                }
            },
        )
    }
}