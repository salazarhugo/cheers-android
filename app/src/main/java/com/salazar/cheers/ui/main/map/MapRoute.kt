package com.salazar.cheers.ui.main.map

import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.navigation.CheersNavigationActions
import kotlinx.coroutines.launch

/**
 * Stateful composable that displays the Navigation route for the Map screen.
 *
 * @param mapViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MapRoute(
    mapViewModel: MapViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by mapViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true,
        )
    }

    MapScreen(
        uiState = uiState,
        onCityChanged = { mapViewModel.updateCity(it) },
        onSelectPost = {
            scope.launch {
                uiState.postSheetState.animateTo(ModalBottomSheetValue.HalfExpanded)
            }
            mapViewModel.selectPost(it)
        },
        navigateToSettingsScreen = { },
        onTogglePublic = mapViewModel::onTogglePublic,
        onAddPostClicked = { navActions.navigateToAddPostSheet() },
        onUserClick = { navActions.navigateToOtherProfile(it) },
        onMapReady = { map, c ->
            scope.launch {
                mapViewModel.mapRepository.onMapReady(map, c)
            }
        },
        )
}