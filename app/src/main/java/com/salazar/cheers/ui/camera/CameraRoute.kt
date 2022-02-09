package com.salazar.cheers.ui.camera

import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.ImageCapture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Camera screen.
 *
 * @param cameraViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CameraRoute(
    cameraViewModel: CameraViewModel,
    navActions: CheersNavigationActions,
) {
    val context = LocalContext.current
    val uiState by cameraViewModel.uiState.collectAsState()
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().setTargetAspectRatio(RATIO_16_9).build()
    }

    CameraScreen(
        uiState = uiState,
        imageCapture = imageCapture,
        onPostClicked = {
            if (uiState.imageUri != null)
                navActions.navigateToAddPostSheetWithPhotoUri(uiState.imageUri.toString())
        },
        onCameraUIAction = { cameraUIAction ->
            when (cameraUIAction) {
                is CameraUIAction.OnSwitchCameraClick ->
                    cameraViewModel.onSwitchCameraClicked()
                is CameraUIAction.OnCameraClick -> {
                    imageCapture.takePicture(context, uiState.lensFacing, { uri, fromGallery ->
                        cameraViewModel.setImageUri(uri)
                    }, {})
                }
                is CameraUIAction.OnGalleryViewClick -> {}
                is CameraUIAction.OnCloseClick -> navActions.navigateBack()
            }
        },
    )
}