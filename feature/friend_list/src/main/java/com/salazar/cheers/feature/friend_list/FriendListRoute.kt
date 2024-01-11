package com.salazar.cheers.feature.friend_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FriendListRoute(
    viewModel: FriendListViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.errorMessage

    FriendListScreen(
        uiState = uiState,
        onFriendListUIAction = { action ->
            when(action) {
                FriendListUIAction.OnBackPressed -> navigateBack()
                FriendListUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                is FriendListUIAction.OnUserClick -> navigateToOtherProfile(action.userId)
                is FriendListUIAction.OnRemoveFriendClick -> viewModel.onRemoveFriend(action.userId)
            }
        },
    )
}
