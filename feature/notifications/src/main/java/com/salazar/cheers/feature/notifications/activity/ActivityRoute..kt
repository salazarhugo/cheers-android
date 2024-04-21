package com.salazar.cheers.feature.notifications.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.ActivityType

@Composable
fun ActivityRoute(
    viewModel: ActivityViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToComments: (String) -> Unit,
    navigateToPostDetail: (String) -> Unit,
    navigateToFriendRequests: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.errorMessage

    ActivityScreen(
        uiState = uiState,
        onActivityUIAction = { action ->
            when(action) {
                is ActivityUIAction.OnActivityClick -> {
                    when (action.activity.type) {
                        ActivityType.NONE -> {}
                        ActivityType.FRIEND_ADDED -> navigateToOtherProfile(
                            action.activity.username
                        )

                        ActivityType.POST_LIKE -> navigateToPostDetail(
                            action.activity.mediaId
                        )

                        ActivityType.STORY_LIKE -> {}
                        ActivityType.COMMENT -> {}
                        ActivityType.MENTION -> navigateToComments(
                            action.activity.mediaId
                        )

                        ActivityType.CREATE_POST -> {}
                        ActivityType.CREATE_EVENT -> {}
                        ActivityType.CREATE_STORY -> {}
                        ActivityType.COMMENT_LIKED -> {}
                    }
                }

                ActivityUIAction.OnBackPressed -> navigateBack()
                ActivityUIAction.OnFriendRequestsClick -> navigateToFriendRequests()
                ActivityUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                is ActivityUIAction.OnUserClick -> navigateToOtherProfile(action.userId)
                is ActivityUIAction.OnPostClick -> navigateToPostDetail(action.postId)
                is ActivityUIAction.OnAddFriendClick -> viewModel.onAddFriendClick(action.userID)
                is ActivityUIAction.OnCancelFriendRequestClick -> viewModel.onCancelFriendRequestClick(
                    userID = action.userID
                )

                is ActivityUIAction.OnRemoveSuggestion -> viewModel.onRemoveSuggestion(action.user)
            }
        },
    )
}
