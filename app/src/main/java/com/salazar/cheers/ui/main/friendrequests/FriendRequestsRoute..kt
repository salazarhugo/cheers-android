package com.salazar.cheers.ui.main.friendrequests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the FriendRequests screen.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun FriendRequestsRoute(
    viewModel: FriendRequestsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FriendRequestsScreen(
        uiState = uiState,
        onFriendRequestsUIAction = { action ->
            when(action) {
                FriendRequestsUIAction.OnBackPressed -> navActions.navigateBack()
                FriendRequestsUIAction.OnSwipeRefresh -> viewModel.onSwipeToRefresh()
                is FriendRequestsUIAction.OnAcceptFriendRequest -> viewModel.onAcceptFriendRequest(action.userId)
                is FriendRequestsUIAction.OnRefuseFriendRequest -> viewModel.onRefuseFriendRequest(action.userId)
                is FriendRequestsUIAction.OnUserClick -> navActions.navigateToOtherProfile(action.userId)
                is FriendRequestsUIAction.OnAddFriendClick -> viewModel.onAddFriendClick(action.userID)
                is FriendRequestsUIAction.OnCancelFriendRequestClick -> viewModel.onCancelFriendRequestClick(userID = action.userID)
                is FriendRequestsUIAction.OnRemoveSuggestion -> viewModel.onRemoveSuggestion(action.user)
            }
        }
    )
}
