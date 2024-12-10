package com.salazar.cheers.core.ui.components.post.footer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PostFooter(
    likeCount: Int,
    commentCount: Int,
    hasViewerLiked: Boolean,
    commentText: String,
    commentUsername: String,
    canLike: Boolean,
    canComment: Boolean,
    canShare: Boolean,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onLikeCountClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PostFooterButtons(
            hasViewerLiked = hasViewerLiked,
            canLike = canLike,
            canComment = canComment,
            canShare = canShare,
            onLike = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
        )
        PostFooterCounters(
            modifier = Modifier,
            likeCount = likeCount,
            commentCount = commentCount,
            onCommentsClick = onCommentClick,
            onLikesClick = onLikeCountClick,
        )
        PostComments(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            commentCount = commentCount,
            onCommentClick = onCommentClick,
            createTime = 0,
            text = commentText,
            username = commentUsername,
        )
    }
}

@ComponentPreviews
@Composable
private fun PostFooterPreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    CheersPreview {
        PostFooter(
            commentCount = 56035,
            likeCount = 89000,
            hasViewerLiked = true,
            canLike = true,
            canComment = true,
            canShare = true,
            commentText = text,
            commentUsername = cheersUserItem.username,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}