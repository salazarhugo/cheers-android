package com.salazar.cheers.ui.main.map

import androidx.compose.material.ModalBottomSheetValue
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

    DisposableEffect(Unit) {
        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = true,
        )
        onDispose {
            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = true,
            )
        }
    }

    MapScreen(
        uiState = uiState,
        onMapUIAction = { action ->
            when (action) {
                MapUIAction.OnBackPressed -> navActions.navigateBack()
                MapUIAction.OnCreatePostClick -> navActions.navigateToCreatePost()
                MapUIAction.OnPublicToggle -> mapViewModel.onTogglePublic()
                MapUIAction.OnSwipeRefresh -> TODO()
                is MapUIAction.OnUserClick -> navActions.navigateToOtherProfile(action.userID)
                is MapUIAction.OnMapReady -> {
                    scope.launch {
                        mapViewModel.mapRepository.onMapReady(action.map, action.ctx)
                    }
                }
                is MapUIAction.OnPostClick -> {
                    scope.launch {
                        uiState.postSheetState.animateTo(ModalBottomSheetValue.HalfExpanded)
                    }
                    mapViewModel.selectPost(action.post)
                }
                MapUIAction.OnMyLocationClick -> mapViewModel.onMyLocationClick()
            }
        },
    )
}