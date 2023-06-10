package com.salazar.cheers.ui.main.detail

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Post detail screen.
 *
 * @param postDetailViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun PostDetailRoute(
    postDetailViewModel: PostDetailViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by postDetailViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
                Toast.makeText(context, "Group chat coming soon", Toast.LENGTH_SHORT).show()
//                FirestoreChat.getOrCreatePostChatGroup((uiState as PostDetailUiState.HasPost).postFeed) {
//                    navActions.navigateToChat(it)
//                }
            },
            onUserClick = { navActions.navigateToOtherProfile(it) },
        )
    else
        com.salazar.cheers.core.share.ui.LoadingScreen()
}