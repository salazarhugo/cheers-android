package com.salazar.cheers.ui.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.UserInput
import com.salazar.cheers.components.comment.Comment
import com.salazar.cheers.internal.CommentWithAuthor

@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    onComment: (String) -> Unit,
    comments: List<CommentWithAuthor>,
) {
    Column {
        Scaffold(
            topBar = { Toolbar(commentCount = comments.size, {}) },
            bottomBar = {
                UserInput(
                    onMessageSent = onComment,
                    resetScroll = { },
                    modifier = Modifier.imePadding(),
                    onImageSelectorClick = {},
                )
            }
        ) {
            Comments(comments = comments)
        }
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
            pushStringAnnotation(tag = "Community Guidelines", annotation = "https://google.com/terms")
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
fun CommentsFooter(
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CommentsFooterIdle()
    }
}

@Composable
fun CommentsFooterIdle() {
    IconButton(
        onClick = {},
    ) {
        Icon(
            Icons.Default.PhotoAlbum,
            contentDescription = null,
            tint = Color.White,
        )
    }
    Text(
        text = "POST",
        textAlign = TextAlign.Center,
        color = Color.White,
    )
}