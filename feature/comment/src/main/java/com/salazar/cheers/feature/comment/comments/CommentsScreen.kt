@file:OptIn(ExperimentalLayoutApi::class)

package com.salazar.cheers.feature.comment.comments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.feature.comment.CommentItem
import com.salazar.cheers.feature.comment.R

@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    onComment: () -> Unit,
    onInputChange: (String) -> Unit,
    onSwipeRefresh: () -> Unit,
    onCommentsUIAction: (CommentsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.comments),
                onBackPressed = { onCommentsUIAction(CommentsUIAction.OnBackPressed) },
            )
        },
        bottomBar = {
            CommentBottomBar(
                avatar = uiState.avatar,
                replyComment = uiState.replyComment,
                onComment = onComment,
                input = uiState.input,
                onInputChange = onInputChange,
                onDeleteReplyCommentClick = { onCommentsUIAction(CommentsUIAction.OnRemoveReplyComment) },
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isRefreshing),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            val comments = uiState.comments
            if (comments == null || uiState.isLoading) {
                CommentsScreenLoading()
            } else {
                Comments(
                    showBanner = uiState.showBanner,
                    comments = comments,
                    onCommentsUIAction = onCommentsUIAction,
                )
            }
        }
    }
}

@Composable
fun Comments(
    showBanner: Boolean,
    comments: List<Comment>,
    onCommentsUIAction: (CommentsUIAction) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .imeNestedScroll(),
    ) {
        if (showBanner) {
            guidelinesBanner(
                onCloseClick = {
                    onCommentsUIAction(CommentsUIAction.OnCloseBannerClick)
                },
            )
        }

        if (comments.isEmpty()) {
            emptyComments()
        }

        items(
            items = comments,
            key = { it.id },
        ) { comment ->
            CommentItem(
                modifier = Modifier.animateItem(),
                comment = comment,
                onLike = { onCommentsUIAction(CommentsUIAction.OnCommentLike(comment.id)) },
                onReply = { onCommentsUIAction(CommentsUIAction.OnReplyClick(comment)) },
                onUserClick = { onCommentsUIAction(CommentsUIAction.OnUserClick(comment.username)) },
                onCommentClicked = {},
                onLongClick = { onCommentsUIAction(CommentsUIAction.OnCommentLongClick(comment.id)) },
                onDoubleClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCommentsUIAction(CommentsUIAction.OnCommentLike(comment.id))
                }
            )
        }
    }
}

private fun LazyListScope.emptyComments() {
    item {
        EmptyCommentsMessage(
            modifier = Modifier.fillParentMaxHeight(),
        )
    }
}

@Composable
fun Caption(
    post: Post,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
    ) {
        UserProfilePicture(
            picture = post.profilePictureUrl,
            modifier = Modifier.size(36.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column {
            val annotatedString = buildAnnotatedString {
                append(post.username)
                if (post.verified) {
                    append(" ")
                    appendInlineContent(id = "imageId")
                    append(" ")
                }
                append(post.caption)
            }
            val inlineContentMap = mapOf(
                "imageId" to InlineTextContent(
                    Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)
                ) {
                    Image(
                        painterResource(com.salazar.cheers.core.ui.R.drawable.ic_verified),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                }
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                inlineContent = inlineContentMap,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = com.salazar.cheers.core.util.relativeTimeFormatter(seconds = post.createTime),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

private fun LazyListScope.guidelinesBanner(
    onCloseClick: () -> Unit,
) {
    item {
        GuidelinesBanner(
            modifier = Modifier.animateItem(),
            onCloseClick = onCloseClick,
        )
        HorizontalDivider()
    }
}

@Composable
private fun GuidelinesBanner(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            val annotatedString = buildAnnotatedString {
                append("Remember to keep comments respectful and to follow our ")
                pushStringAnnotation(
                    tag = "Community Guidelines",
                    annotation = "https://google.com/terms"
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSecondaryContainer)) {
                    append("Community Guidelines")
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        IconButton(
            onClick = onCloseClick,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close icon",
            )
        }
    }
}

@Composable
fun ReplyComment(
    comment: Comment,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Replying to ${comment.username}",
//            style = MaterialTheme.typography.labelMedium,
        )
        IconButton(
//            modifier = Modifier.padding(8.dp),
            onClick = onRemove,
        ) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
    }
}

@Composable
fun CommentBottomBar(
    avatar: String?,
    input: String,
    onComment: () -> Unit,
    onInputChange: (String) -> Unit,
    replyComment: Comment? = null,
    onDeleteReplyCommentClick: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .navigationBarsPadding()
    ) {
        if (replyComment != null) {
            ReplyComment(
                comment = replyComment,
                onRemove = onDeleteReplyCommentClick,
            )
        }
        HorizontalDivider(
            thickness = 0.5.dp,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusRequester.requestFocus() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarComponent(
                    avatar = avatar,
                    size = 40.dp,
                )
                Spacer(modifier = Modifier.width(16.dp))
                BasicTextField(
                    value = input,
                    onValueChange = { onInputChange(it) },
                    modifier = Modifier.focusRequester(focusRequester = focusRequester),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onComment() },
                    ),
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                    decorationBox = {
                        if (input.isBlank())
                            Text(
                                text = "Add a comment...",
                            )
                        it()
                    },
                )
            }
            TextButton(
                onClick = onComment,
                enabled = input.isNotBlank()
            ) {
                Text(
                    text = stringResource(id = R.string.post),
                )
            }
        }
    }
}

