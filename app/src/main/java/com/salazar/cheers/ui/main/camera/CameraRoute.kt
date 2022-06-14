package com.salazar.cheers.ui.main.camera

import androidx.activity.compose.BackHandler
import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.ImageCapture
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.components.FunctionalityNotAvailablePanel
import com.salazar.cheers.navigation.CheersNavigationActions
import kotlinx.coroutines.launch

/**
 * Stateful composable that displays the Navigation route for the Camera screen.
 *
 * @param cameraViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CameraRoute(
    cameraViewModel: CameraViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by cameraViewModel.uiState.collectAsState()
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().setTargetAspectRatio(RATIO_16_9).build()
    }

    BackHandler(enabled = true) {
        cameraViewModel.setImageUri(null)
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
        scrimColor = Color.Transparent
    ) {
        CameraScreen(
            uiState = uiState,
            imageCapture = imageCapture,
            onStoryClick = {
                cameraViewModel.uploadStory()
                navActions.navigateBack()
            },
            onPostClicked = {
                if (uiState.imageUri != null)
                    navActions.navigateToAddPostSheetWithPhotoUri(uiState.imageUri.toString())
            },
            onCameraUIAction = { cameraUIAction ->
                when (cameraUIAction) {
                    is CameraUIAction.OnSwitchCameraClick ->
                        cameraViewModel.onSwitchCameraClicked()
                    is CameraUIAction.OnCameraClick -> {
                        imageCapture.takePicture(
                            context,
                            uiState.lensFacing,
                            uiState.flashMode,
                            { uri, fromGallery ->
                                cameraViewModel.setImageUri(uri)
                            },
                            {})
                    }
                    is CameraUIAction.OnGalleryViewClick -> {}
                    is CameraUIAction.OnCloseClick -> navActions.navigateBack()
                    is CameraUIAction.OnBackClick -> {
                        cameraViewModel.setImageUri(null)
                    }
                    is CameraUIAction.OnFlashClick -> {
                        cameraViewModel.onSwitchFlash()
                    }
                    is CameraUIAction.OnAddContent -> {
                        scope.launch {
                            sheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                }
            },
        )
    }
}