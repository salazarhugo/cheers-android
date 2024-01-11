package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Room screen.
 *
 * @param roomViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun RoomRoute(
    roomViewModel: RoomViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
    showSnackBar: (String) -> Unit,
) {
    val uiState by roomViewModel.uiState.collectAsStateWithLifecycle()

//    if (uiState.) {
//        LaunchedEffect(Unit) {
//            showSnackBar(uiState.lxx!!)
//        }
//    }

    when (uiState) {
        is RoomUiState.NoRoom -> LoadingScreen()
        is RoomUiState.HasRoom -> {
            RoomScreen(
                uiState = uiState as RoomUiState.HasRoom,
                onLeaveChat = {
                    roomViewModel.onLeaveRoom()
//                    navigateToMessages()
                },
                onBackPressed = { navActions.navigateBack() },
                onUserClick = { navActions.navigateToOtherProfile(it) },
            )
        }
    }
}
