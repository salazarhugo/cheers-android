package com.salazar.cheers.ui.comment

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import com.salazar.cheers.components.UserInput
import com.salazar.cheers.components.comment.Comment

@Composable
fun CommentsScreen(
    uiState: CommentsUiState,
    modifier: Modifier = Modifier,
) {
    Column {
        Scaffold(
            modifier = Modifier,
            topBar = { Toolbar(18, {}) },
            bottomBar = {
                UserInput(
                    onMessageSent = { },
                    resetScroll = { },
                    modifier = Modifier.imePadding(),
                    onImageSelectorClick = {},
                )
            }
        ) {
            Comments(listOf("wf", "fw", "fw"))
        }
    }
}

@Composable
fun Comments(comments: List<String>) {
    LazyColumn() {
        items(comments) { comment ->
            Comment(
                profilePictureUrl = "",
                username = "cheers",
                verified = true,
                text = "J'arrive les boys",
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$commentCount comments",
            style = MaterialTheme.typography.titleSmall,
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