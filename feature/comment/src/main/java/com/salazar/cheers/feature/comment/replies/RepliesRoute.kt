package com.salazar.cheers.feature.comment.replies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RepliesRoute(
    repliesViewModel: RepliesViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
    navigateToCommentMoreSheet: (String) -> Unit = {},
) {
    val uiState by repliesViewModel.uiState.collectAsStateWithLifecycle()

    RepliesScreen(
        uiState = uiState,
        onComment = repliesViewModel::onComment,
        onInputChange = repliesViewModel::onInputChange,
        onSwipeRefresh = { repliesViewModel.onSwipeRefresh() },
        onRepliesUIAction = { action ->
            when(action) {
                RepliesUIAction.OnBackPressed -> navigateBack()
                RepliesUIAction.OnFriendRequestsClick -> TODO()
                RepliesUIAction.OnSwipeRefresh -> TODO()
                is RepliesUIAction.OnUserClick -> TODO()
                is RepliesUIAction.OnCommentLongClick -> navigateToCommentMoreSheet(action.commentID)
                RepliesUIAction.OnRemoveReplyComment -> TODO()
                is RepliesUIAction.OnReplyClick -> TODO()
                is RepliesUIAction.OnCommentLike -> repliesViewModel.onLike(action.commentID)
            }
        }
    )
}