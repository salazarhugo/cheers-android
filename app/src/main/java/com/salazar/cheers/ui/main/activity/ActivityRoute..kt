package com.salazar.cheers.ui.main.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.internal.ActivityType
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState

/**
 * Stateful composable that displays the Navigation route for the Activity screen.
 *
 * @param activityViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ActivityRoute(
    appState: CheersAppState,
    viewModel: ActivityViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    if (errorMessage != null) {
        LaunchedEffect(appState.snackBarHostState) {
            appState.showSnackBar(errorMessage)
        }
    }

    ActivityScreen(
        uiState = uiState,
        onActivityUIAction = { action ->
            when(action) {
                is ActivityUIAction.OnActivityClick -> {
                    when(action.activity.type) {
                        ActivityType.NONE -> {}
                        ActivityType.FRIEND_ADDED -> navActions.navigateToOtherProfile(action.activity.username)
                        ActivityType.POST_LIKE -> navActions.navigateToPostDetail(action.activity.mediaId)
                        ActivityType.STORY_LIKE -> {}
                        ActivityType.COMMENT -> {}
                        ActivityType.MENTION -> navActions.navigateToComments(action.activity.mediaId)
                        ActivityType.CREATE_POST -> {}
                        ActivityType.CREATE_EVENT -> {}
                        ActivityType.CREATE_STORY -> {}
                        ActivityType.COMMENT_LIKED -> {}
                    }
                }
                ActivityUIAction.OnBackPressed -> navActions.navigateBack()
                ActivityUIAction.OnFriendRequestsClick -> navActions.navigateToFriendRequests()
                ActivityUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                is ActivityUIAction.OnUserClick -> navActions.navigateToOtherProfile(action.userId)
                is ActivityUIAction.OnPostClick -> navActions.navigateToPostDetail(action.postId)
                is ActivityUIAction.OnAddFriendClick -> viewModel.onAddFriendClick(action.userID)
                is ActivityUIAction.OnCancelFriendRequestClick -> viewModel.onCancelFriendRequestClick(userID = action.userID)
                is ActivityUIAction.OnRemoveSuggestion -> viewModel.onRemoveSuggestion(action.user)
            }
        },
    )
}
