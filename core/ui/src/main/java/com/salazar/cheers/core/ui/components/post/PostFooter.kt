package com.salazar.cheers.core.ui.components.post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.extensions.noRippleClickable

@Composable
fun PostFooter(
    likeCount: Int,
    commentCount: Int,
    hasViewerLiked: Boolean,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onLikeCountClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        PostFooterButtons(
            hasViewerLiked = hasViewerLiked,
            onLike = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
        )
        PostFooterCounters(
            likeCount = likeCount,
            commentCount = commentCount,
            onCommentsClick = onCommentClick,
            onLikesClick = onLikeCountClick,
        )
        PostComments(
            commentCount = commentCount,
            onCommentClick = { onCommentClick() },
            createTime = 0,
            text = "",
            username = "",
        )
    }
}

@Composable
fun PostComments(
    text: String,
    username: String,
    createTime: Long,
    commentCount: Int,
    onCommentClick: () -> Unit = {},
) {
    if (text.isBlank())
        return

    PostLastComment(
        text = text,
        username = username,
        createTime = createTime,
    )

    val text = "View all $commentCount comments"

    if (commentCount > 1) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            modifier = Modifier
                .noRippleClickable {
                    onCommentClick()
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }

}

@Composable
fun PostLastComment(
    username: String,
    text: String,
    createTime: Long,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(text = username)
            }
            append(" ")
            append(text)
        }
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun DrunkennessLevelIndicator(
    drunkenness: Int,
) {
    var show by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { show = !show }
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (show)
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "Drunkenness out of 100",
                style = MaterialTheme.typography.bodyMedium,
            )
        else {
            Icon(
                Icons.Outlined.WaterDrop,
                contentDescription = null,
            )
//            Image(
//                painter = rememberImagePainter(R.drawable.ic_tequila_shot),
//                modifier = Modifier.size(24.dp),
//                contentDescription = null,
//            )
            Text(
                text = drunkenness.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
//        for (i in 1..5) {
//            val icon =
//                when {
//                    drunkenness*5/100 >= i -> Icons.Default.Star
//                    drunkenness*5/100 > i - 1 + 0.5 -> Icons.Default.StarHalf
//                    else -> Icons.Default.StarOutline
//                }
//            Icon(
//                icon,
//                contentDescription = null,
//                tint = Color(0xFFFFD700)
//            )
//        }
    }
}

@ComponentPreviews
@Composable
fun PostFooterPreview() {
    CheersPreview {
        PostFooter(
            commentCount = 56035,
            likeCount = 89000,
            hasViewerLiked = true,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}