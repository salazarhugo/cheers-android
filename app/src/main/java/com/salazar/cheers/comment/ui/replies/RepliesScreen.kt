package com.salazar.cheers.comment.ui.replies

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.comment.ui.CommentItem
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.theme.GreySheet
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import androidx.compose.material3.Divider

@Composable
fun RepliesScreen(
    uiState: RepliesUiState,
    profilePictureUrl: String,
    onComment: () -> Unit,
    onBackPressed: () -> Unit,
    onInputChange: (String) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRepliesUIAction: (RepliesUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.replies),
                onBackPressed = onBackPressed,
            )
        },
        bottomBar = {
            CommentBottomBar(
                uiState = uiState,
                onComment = onComment,
                onInputChange = onInputChange,
                profilePictureUrl = profilePictureUrl,
                onRepliesUIAction = onRepliesUIAction,
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
            Divider()
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

@Composable
fun Caption(
    post: com.salazar.cheers.data.post.repository.Post,
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
                        painterResource(R.drawable.ic_verified),
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
                    text = com.salazar.cheers.core.util.relativeTimeFormatter(epoch = post.createTime),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun GuidelinesBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp)
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
}

@Composable
fun CommentBottomBar(
    uiState: RepliesUiState,
    profilePictureUrl: String,
    onComment: () -> Unit,
    onInputChange: (String) -> Unit,
    onRepliesUIAction: (RepliesUIAction) -> Unit,
) {
    val color =
        if (isSystemInDarkTheme()) GreySheet else MaterialTheme.colorScheme.background
    val focusRequester = remember { FocusRequester() }
    val input = uiState.input

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .background(color = color)
    ) {
        Divider()
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
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = profilePictureUrl)
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                                error(R.drawable.default_profile_picture)
                            }).build()
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentDescription = null,
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
                                text = "Add a reply...",
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
//                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

