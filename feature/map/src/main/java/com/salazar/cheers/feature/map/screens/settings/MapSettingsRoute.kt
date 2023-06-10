package com.salazar.cheers.feature.map.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.map.ui.dialogs.BottomSheetM3

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
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    BottomSheetM3 {
        MapSettingsScreen(
            uiState = uiState,
            onMapSettingsUIAction = { action ->
                when (action) {
                    MapSettingsUIAction.OnBackPressed -> navActions.navigateBack()
                    MapSettingsUIAction.OnSwipeRefresh -> TODO()
                    is MapSettingsUIAction.OnGhostModeChange -> mapViewModel.onGhostModeChange(action.enabled)
                }
            },
        )
    }
}
