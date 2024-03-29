package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun RoomRoute(
    roomViewModel: RoomViewModel = hiltViewModel(),
    showSnackBar: (String) -> Unit,
    navigateBack: () -> Unit = {},
    navigateToUserProfile: (String) -> Unit,
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
                onBackPressed = navigateBack,
                onUserClick = navigateToUserProfile,
            )
        }
    }
}
