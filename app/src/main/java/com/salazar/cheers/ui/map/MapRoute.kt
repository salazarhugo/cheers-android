package com.salazar.cheers.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Stateful composable that displays the Navigation route for the Interests screen.
 *
 * @param homeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MapRoute(
    mapViewModel: MapViewModel,
) {
    val uiState by mapViewModel.uiState.collectAsState()
    MapScreen(
        uiState = uiState,
        onCityChanged = { mapViewModel.updateCity(it) },
        onSelectPost = { mapViewModel.selectPost(it) },
        navigateToSettingsScreen = { }
    )
}