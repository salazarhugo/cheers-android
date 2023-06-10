package com.salazar.cheers.ui.main.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                    when (action.activity.type) {
                        com.salazar.cheers.data.activity.ActivityType.NONE -> {}
                        com.salazar.cheers.data.activity.ActivityType.FRIEND_ADDED -> navActions.navigateToOtherProfile(
                            action.activity.username
                        )

                        com.salazar.cheers.data.activity.ActivityType.POST_LIKE -> navActions.navigateToPostDetail(
                            action.activity.mediaId
                        )

                        com.salazar.cheers.data.activity.ActivityType.STORY_LIKE -> {}
                        com.salazar.cheers.data.activity.ActivityType.COMMENT -> {}
                        com.salazar.cheers.data.activity.ActivityType.MENTION -> navActions.navigateToComments(
                            action.activity.mediaId
                        )

                        com.salazar.cheers.data.activity.ActivityType.CREATE_POST -> {}
                        com.salazar.cheers.data.activity.ActivityType.CREATE_EVENT -> {}
                        com.salazar.cheers.data.activity.ActivityType.CREATE_STORY -> {}
                        com.salazar.cheers.data.activity.ActivityType.COMMENT_LIKED -> {}
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
