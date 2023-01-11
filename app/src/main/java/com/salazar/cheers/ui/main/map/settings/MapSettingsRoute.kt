package com.salazar.cheers.ui.main.map.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Map screen.
 *
 * @param mapViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MapSettingsRoute(
    mapViewModel: MapSettingsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by mapViewModel.uiState.collectAsState()

    MapSettingsScreen(
        uiState = uiState,
        onMapSettingsUIAction = { action ->
            when (action) {
                MapSettingsUIAction.OnBackPressed -> navActions.navigateBack()
                MapSettingsUIAction.OnSwipeRefresh -> TODO()
            }
        },
    )
}