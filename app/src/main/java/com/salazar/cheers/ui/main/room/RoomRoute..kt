package com.salazar.cheers.ui.main.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Room screen.
 *
 * @param roomViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun RoomRoute(
    roomViewModel: RoomViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by roomViewModel.uiState.collectAsState()

    when (uiState) {
        is RoomUiState.NoRoom -> LoadingScreen()
        is RoomUiState.HasRoom -> {
            RoomScreen(
                uiState = uiState as RoomUiState.HasRoom,
                onLeaveChat = {
                    roomViewModel.onLeaveRoom()
                    navActions.navigateToMessages()
                },
                onBackPressed = { navActions.navigateBack() },
                onUserClick = { navActions.navigateToOtherProfile(it) },
            )
        }
    }
}