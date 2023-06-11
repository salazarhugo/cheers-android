package com.salazar.cheers.core.ui.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.salazar.cheers.data.post.repository.Post
import com.salazar.common.ui.extensions.noRippleClickable

@Composable
fun PostFooter(
    post: Post,
    onLike: (post: Post) -> Unit,
    onCommentClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    pagerState: PagerState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {

//        if (post.photos.size > 1)
//            HorizontalPagerIndicator(
//                pagerState = pagerState,
//                modifier = Modifier
//                    .padding(top = 8.dp)
//                    .align(Alignment.CenterHorizontally),
//                activeColor = MaterialTheme.colorScheme.primary,
//            )
//        Text(postFeed.post.tagUsersId.toString())
//        Text(postFeed.tagUsers.toString())
            PostFooterButtons(
                post = post,
                onLike = onLike,
                onCommentClick = onCommentClick,
                onShareClick = onShareClick,
            )
        if (post.type != com.salazar.cheers.data.post.repository.PostType.TEXT) {
//            LikedBy(post = postFeed.post)
//            if (post.tagUsers.isNotEmpty())
//                TagUsers(postFeed.tagUsers)
        }
        PostComments(
            post = post,
            onCommentClick = { onCommentClick(post.id) },
        )
    }
    if (post.type != com.salazar.cheers.data.post.repository.PostType.TEXT)
        Spacer(Modifier.height(12.dp))
}

@Composable
fun PostComments(
    post: com.salazar.cheers.data.post.repository.Post,
    onCommentClick: () -> Unit,
) {
    if (post.lastCommentText.isBlank())
        return

    val commentCount = post.comments

    PostLastComment(
        username = post.lastCommentUsername,
        text = post.lastCommentText,
        createTime = post.lastCommentCreateTime,
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
fun PostFooterButtons(
    post: com.salazar.cheers.data.post.repository.Post,
    onLike: (post: com.salazar.cheers.data.post.repository.Post) -> Unit,
    onCommentClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LikeButton(
                like = post.liked,
                likes = post.likes,
                onToggle = { onLike(post) },
            )
            CommentButton(
                comments = post.comments,
                onClick = { onCommentClick(post.id) },
            )
            IconButton(onClick = { onShareClick(post.id) }) {
                Icon(Icons.Outlined.Share, null)
            }
        }
        if (post.drunkenness > 0)
            DrunkennessLevelIndicator(drunkenness = post.drunkenness)
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
