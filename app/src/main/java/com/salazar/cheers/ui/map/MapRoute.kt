package com.salazar.cheers.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Map screen.
 *
 * @param mapViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MapRoute(
    mapViewModel: MapViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by mapViewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onCityChanged = { mapViewModel.updateCity(it) },
        onSelectPost = { mapViewModel.selectPost(it) },
        navigateToSettingsScreen = { },
        onTogglePublic = mapViewModel::onTogglePublic,
        onAddPostClicked = { navActions.navigateToAddPostSheet() },
    )
}