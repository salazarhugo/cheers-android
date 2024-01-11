package com.salazar.cheers.feature.map.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun MapSettingsRoute(
    mapViewModel: MapSettingsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    MapSettingsScreen(
        uiState = uiState,
        onMapSettingsUIAction = { action ->
            when (action) {
                MapSettingsUIAction.OnBackPressed -> navigateBack()
                MapSettingsUIAction.OnSwipeRefresh -> TODO()
                is MapSettingsUIAction.OnGhostModeChange -> mapViewModel.onGhostModeChange(action.enabled)
            }
        },
    )
}
