package com.salazar.cheers.ui.main.comment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.imePadding
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.comment.Comment
import com.salazar.cheers.internal.CommentWithAuthor
import com.salazar.cheers.ui.theme.GreySheet

@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    profilePictureUrl: String,
    onComment: () -> Unit,
    onInputChange: (String) -> Unit,
    comments: List<CommentWithAuthor>,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    Scaffold(
        topBar = { Toolbar(commentCount = comments.size, {}) },
        bottomBar = {
            val y = bottomSheetNavigator.navigatorSheetState.offset.value
            CommentBottomBar(
                input = uiState.input,
                y = y,
                onComment = onComment,
                onInputChange = onInputChange,
                profilePictureUrl = profilePictureUrl,
            )
        }
    ) {
        Comments(comments = comments)
    }
}

@Composable
fun Comments(comments: List<CommentWithAuthor>) {
    LazyColumn() {
        item {
            GuidelinesBanner()
        }
        items(comments) { commentWithAuthor ->
            val author = commentWithAuthor.author
            val comment = commentWithAuthor.comment
            Comment(
                profilePictureUrl = author.profilePictureUrl,
                username = author.username,
                verified = author.verified,
                text = comment.text,
                created = comment.created,
                onLike = {},
                onReply = {},
                onCommentClicked = {},
            )
        }
    }
}

@Composable
fun Toolbar(
    commentCount: Int,
    onCloseClicked: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Comments",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = commentCount.toString(),
                style = MaterialTheme.typography.titleSmall,
            )
        }
        DividerM3()
    }
}

@Composable
fun GuidelinesBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        val annotatedString = buildAnnotatedString {
            append("Remember to keep comments respectful and to follow our ")
            pushStringAnnotation(
                tag = "Community Guidelines",
                annotation = "https://google.com/terms"
            )
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
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
    y: Float,
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
            .offset(y = -LocalDensity.current.run { y.toDp() })
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
                    painter = rememberImagePainter(
                        data = profilePictureUrl,
                        builder = {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        },
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
                        onSend = {
                        }
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
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

