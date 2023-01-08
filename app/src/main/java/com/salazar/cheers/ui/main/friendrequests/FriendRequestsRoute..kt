package com.salazar.cheers.ui.main.friendrequests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the FriendRequests screen.
 *
 * @param friendRequestsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun FriendRequestsRoute(
    friendRequestsViewModel: FriendRequestsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by friendRequestsViewModel.uiState.collectAsState()

    FriendRequestsScreen(
        uiState = uiState,
        onFriendRequestsUIAction = { action ->
            when(action) {
                FriendRequestsUIAction.OnBackPressed -> navActions.navigateBack()
                FriendRequestsUIAction.OnSwipeRefresh -> friendRequestsViewModel.onSwipeToRefresh()
                is FriendRequestsUIAction.OnAcceptFriendRequest -> friendRequestsViewModel.onAcceptFriendRequest(action.userId)
            }
        }
    )
}
