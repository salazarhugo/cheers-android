package com.salazar.cheers.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CameraRoute(
    cameraViewModel: CameraViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by cameraViewModel.uiState.collectAsState()
    CameraScreen(
        uiState = uiState,
        navActions = navActions,
        onTakePhoto = { cameraViewModel.setImageUri(it) }
    )
}