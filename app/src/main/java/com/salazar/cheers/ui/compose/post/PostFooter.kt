package com.salazar.cheers.ui.compose.post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.ui.compose.animations.Bounce
import com.salazar.cheers.ui.compose.extensions.noRippleClickable

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

        if (post.photos.size > 1)
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                activeColor = MaterialTheme.colorScheme.primary,
            )
//        Text(postFeed.post.tagUsersId.toString())
//        Text(postFeed.tagUsers.toString())
        PostFooterButtons(
            post = post,
            onLike = onLike,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
        )
        if (post.type != PostType.TEXT) {
//            LikedBy(post = postFeed.post)
//            if (post.tagUsers.isNotEmpty())
//                TagUsers(postFeed.tagUsers)
        }
        PostComments(
            post = post,
            onCommentClick = { onCommentClick(post.id) },
        )
    }
    if (post.type != PostType.TEXT)
        Spacer(Modifier.height(12.dp))
}

@Composable
fun PostComments(
    post: Post,
    onCommentClick: () -> Unit,
) {
    if (post.lastCommentText.isBlank())
        return

    val commentCount = post.comments

    val text = if (commentCount > 1)
        "View all $commentCount comments"
    else
        "View 1 comment"

    PostLastComment(
        username = post.lastCommentUsername,
        text = post.lastCommentText,
        createTime = post.lastCommentCreateTime,
    )
    if (commentCount > 0)
        Spacer(Modifier.height(4.dp))
        Text(
            text = text,
            modifier = Modifier
                .noRippleClickable {
                    onCommentClick()
                },
            style = MaterialTheme.typography.labelMedium,
        )

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
    post: Post,
    onLike: (post: Post) -> Unit,
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
            Bounce(onBounce = { onCommentClick(post.id) }) {
                Icon(
                    painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                    contentDescription = null
                )
            }
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
