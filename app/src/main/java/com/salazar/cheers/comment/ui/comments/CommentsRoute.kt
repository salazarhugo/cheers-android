package com.salazar.cheers.comment.ui.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.comment.ui.replies.RepliesUIAction
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Comments screen.
 *
 * @param commentsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CommentsRoute(
    commentsViewModel: CommentsViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by commentsViewModel.uiState.collectAsState()

    CommentsScreen(
        uiState = uiState,
        onComment = commentsViewModel::onComment,
        profilePictureUrl = uiState.user?.picture ?: "",
        onInputChange = commentsViewModel::onInputChange,
        onSwipeRefresh = { commentsViewModel.onSwipeRefresh() },
        onCommentsUIAction = { action ->
            when(action) {
                CommentsUIAction.OnBackPressed -> navActions.navigateBack()
                CommentsUIAction.OnFriendRequestsClick -> TODO()
                is CommentsUIAction.OnReplyClick -> {
                    navActions.navigateToCommentReplies(action.comment.id)
                }
                CommentsUIAction.OnSwipeRefresh -> TODO()
                is CommentsUIAction.OnUserClick -> TODO()
                CommentsUIAction.OnRemoveReplyComment -> commentsViewModel.onRemoveReplyComment()
                is CommentsUIAction.OnCommentLongClick -> navActions.navigateToCommentMoreSheet(action.commentID)
                is CommentsUIAction.OnCommentLike -> commentsViewModel.onLike(action.commentID)
            }
        }
    )
}