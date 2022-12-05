package com.salazar.cheers.ui.main.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.compose.DividerM3
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.Toolbar
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.relativeTimeFormatter
import com.salazar.cheers.ui.theme.GreySheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    profilePictureUrl: String,
    onComment: () -> Unit,
    onBackPressed: () -> Unit,
    onInputChange: (String) -> Unit,
    onDeleteComment: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(title = "Comments", onBackPressed = onBackPressed)
        },
        bottomBar = {
            CommentBottomBar(
                input = uiState.input,
                onComment = onComment,
                onInputChange = onInputChange,
                profilePictureUrl = profilePictureUrl,
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = { Unit },
            modifier = Modifier.padding(it),
        ) {
            Comments(
                comments = uiState.comments,
                post = uiState.post,
                onDeleteComment = onDeleteComment
            )
        }
    }
}

@Composable
fun Comments(
    comments: List<Comment>?,
    post: Post?,
    onDeleteComment: (String) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        item {
            GuidelinesBanner()
        }
        if (post != null && post.caption.isNotBlank())
            item {
                Caption(post = post)
                DividerM3()
            }
        if (comments != null)
            items(comments) { comment ->
                com.salazar.cheers.compose.comment.Comment(
                    comment = comment,
                    onLike = {},
                    onReply = {},
                    onCommentClicked = {},
                    onDeleteComment = onDeleteComment,
                )
            }
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
                    text = relativeTimeFormatter(epoch = post.createTime),
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
    input: String,
    profilePictureUrl: String,
    onComment: () -> Unit,
    onInputChange: (String) -> Unit
) {
    val color = if (isSystemInDarkTheme()) GreySheet else MaterialTheme.colorScheme.background
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        DividerM3()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = color)
                .clickable { focusRequester.requestFocus() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
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
//                    keyboardType = keyboardType,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onComment() },
                    ),
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                )
            }
            TextButton(
                onClick = onComment,
                enabled = input.isNotBlank()
            ) {
                Text(
                    text = "Post",
//                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

