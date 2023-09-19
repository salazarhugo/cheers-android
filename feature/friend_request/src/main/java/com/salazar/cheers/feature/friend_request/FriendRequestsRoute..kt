package com.salazar.cheers.feature.friend_request

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FriendRequestsRoute(
    viewModel: FriendRequestsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FriendRequestsScreen(
        uiState = uiState,
        onFriendRequestsUIAction = { action ->
            when(action) {
                FriendRequestsUIAction.OnBackPressed -> navigateBack()
                FriendRequestsUIAction.OnSwipeRefresh -> viewModel.onSwipeToRefresh()
                is FriendRequestsUIAction.OnAcceptFriendRequest -> viewModel.onAcceptFriendRequest(action.userId)
                is FriendRequestsUIAction.OnRefuseFriendRequest -> viewModel.onRefuseFriendRequest(action.userId)
                is FriendRequestsUIAction.OnUserClick -> navigateToOtherProfile(action.userId)
                is FriendRequestsUIAction.OnAddFriendClick -> viewModel.onAddFriendClick(action.userID)
                is FriendRequestsUIAction.OnCancelFriendRequestClick -> viewModel.onCancelFriendRequestClick(userID = action.userID)
                is FriendRequestsUIAction.OnRemoveSuggestion -> viewModel.onRemoveSuggestion(action.user)
            }
        }
    )
}
