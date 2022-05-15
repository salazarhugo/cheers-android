package com.salazar.cheers.ui.main.camera

import androidx.activity.compose.BackHandler
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.navigation.CheersNavigationActions
import androidx.compose.ui.platform.LocalContext

/**
 * Stateful composable that displays the Navigation route for the ChatCamera screen.
 *
 * @param chatCameraViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ChatCameraRoute(
    chatCameraViewModel: ChatCameraViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by chatCameraViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
    }

    BackHandler(enabled = true) {
        chatCameraViewModel.setImageUri(null)
    }

    SystemBarColors()

    ChatCameraScreen(
        uiState = uiState,
        imageCapture = imageCapture,
        onCameraUIAction = { cameraUIAction ->
            when (cameraUIAction) {
                is CameraUIAction.OnSwitchCameraClick ->
                    chatCameraViewModel.onSwitchCameraClicked()
                is CameraUIAction.OnCameraClick -> {
                    chatCameraViewModel.onCameraClick()
                    imageCapture.takePicture(
                        context,
                        uiState.lensFacing,
                        uiState.flashMode,
                        { uri, fromGallery ->
                            chatCameraViewModel.setImageUri(uri)
                        },
                        {})
                }
                is CameraUIAction.OnSendClick -> {
                    chatCameraViewModel.sendImage()
                    navActions.navigateBack()
                }
                is CameraUIAction.OnCloseClick -> navActions.navigateBack()
                is CameraUIAction.OnFlashClick -> {
                    chatCameraViewModel.onSwitchFlash()
                }
            }
        },
    )
}

@Composable
fun SystemBarColors() {
    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(
            Color.Black,
            darkIcons = false,
        )
        onDispose {
            systemUiController.setSystemBarsColor(
                if (darkTheme) Color.Black else Color.White,
                darkIcons = !darkTheme
            )
        }
    }
}