package com.salazar.cheers.feature.comment.replies

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.feature.comment.CommentItem
import com.salazar.cheers.feature.comment.comments.CommentBottomBar

@Composable
fun RepliesScreen(
    uiState: RepliesUiState,
    onComment: () -> Unit,
    onInputChange: (String) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRepliesUIAction: (RepliesUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = com.salazar.cheers.core.ui.R.string.replies),
                onBackPressed = { onRepliesUIAction(RepliesUIAction.OnBackPressed) },
            )
        },
        bottomBar = {
            CommentBottomBar(
                avatar = uiState.account?.picture,
                onComment = onComment,
                onInputChange = onInputChange,
                input = uiState.input,
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            val replies = uiState.replies
            val comment = uiState.comment
            if (replies == null || comment == null || uiState.isLoading)
                LoadingScreen()
            else
                Replies(
                    parentComment = comment,
                    comments = replies,
                    onRepliesUIAction = onRepliesUIAction,
                )
        }
    }
}

@Composable
fun Replies(
    parentComment: Comment,
    comments: List<Comment>,
    onRepliesUIAction: (RepliesUIAction) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        item {
            CommentItem(
                modifier = Modifier.animateItemPlacement(),
                comment = parentComment,
                onLike = {
                    onRepliesUIAction(RepliesUIAction.OnCommentLike(parentComment.id))
                },
                onReply = {},
                onCommentClicked = {},
                readOnly = true,
                onLongClick = {
                    onRepliesUIAction(RepliesUIAction.OnCommentLongClick(parentComment.id))
                },
            )
            HorizontalDivider()
        }
        items(comments, key = { it.id }) { comment ->
            CommentItem(
                modifier = Modifier.animateItemPlacement(),
                padding = PaddingValues(start = 60.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
                comment = comment,
                onLike = {
                    onRepliesUIAction(RepliesUIAction.OnCommentLike(comment.id))
                },
                onReply = {
                    onRepliesUIAction(RepliesUIAction.OnReplyClick(comment))
                },
                onCommentClicked = {},
                onLongClick = {
                    onRepliesUIAction(RepliesUIAction.OnCommentLongClick(comment.id))
                }
            )
        }
    }
}
