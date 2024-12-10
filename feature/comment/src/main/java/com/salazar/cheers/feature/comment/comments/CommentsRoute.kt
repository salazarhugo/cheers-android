package com.salazar.cheers.feature.comment.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CommentsRoute(
    commentsViewModel: CommentsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    navigateToCommentMoreSheet: (String) -> Unit,
    navigateToCommentReplies: (String) -> Unit,
) {
    val uiState by commentsViewModel.uiState.collectAsStateWithLifecycle()

    CommentsScreen(
        uiState = uiState,
        onComment = commentsViewModel::onComment,
        profilePictureUrl = uiState.avatar,
        onInputChange = commentsViewModel::onInputChange,
        onSwipeRefresh = { commentsViewModel.onSwipeRefresh() },
        onCommentsUIAction = { action ->
            when(action) {
                CommentsUIAction.OnBackPressed -> onBackPressed()
                CommentsUIAction.OnFriendRequestsClick -> TODO()
                is CommentsUIAction.OnReplyClick -> {
                    navigateToCommentReplies(action.comment.id)
                }
                CommentsUIAction.OnSwipeRefresh -> TODO()
                is CommentsUIAction.OnUserClick -> TODO()
                CommentsUIAction.OnRemoveReplyComment -> commentsViewModel.onRemoveReplyComment()
                is CommentsUIAction.OnCommentLongClick -> navigateToCommentMoreSheet(action.commentID)
                is CommentsUIAction.OnCommentLike -> commentsViewModel.onLike(action.commentID)
                CommentsUIAction.OnCloseBannerClick -> commentsViewModel.onCloseBannerClick()
            }
        }
    )
}