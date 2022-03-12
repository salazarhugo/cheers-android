package com.salazar.cheers.ui.main.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.util.FirestoreChat

/**
 * Stateful composable that displays the Navigation route for the Post detail screen.
 *
 * @param postDetailViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun PostDetailRoute(
    postDetailViewModel: PostDetailViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by postDetailViewModel.uiState.collectAsState()

    if (uiState is PostDetailUiState.HasPost)
        PostDetailScreen(
            uiState = uiState as PostDetailUiState.HasPost,
            onHeaderClicked = { navActions.navigateToOtherProfile(it) },
            onBackPressed = { navActions.navigateBack() },
            onDelete = {
                postDetailViewModel.deletePost()
                navActions.navigateBack()
            },
            onLeave = {
                postDetailViewModel.leavePost()
                navActions.navigateBack()
            },
            onMapClick = { navActions.navigateToMap() },
            onToggleLike = postDetailViewModel::toggleLike,
            onMessageClicked = {
                FirestoreChat.getOrCreatePostChatGroup((uiState as PostDetailUiState.HasPost).postFeed) {
                    navActions.navigateToChat(it)
                }
            }
        )
    else
        LoadingScreen()
}