package com.salazar.cheers.comment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.core.data.internal.relativeTimeFormatter
import com.salazar.cheers.ui.compose.Username
import com.salazar.cheers.ui.compose.animations.AnimatedTextCounter
import com.salazar.cheers.ui.compose.extensions.noRippleClickable
import com.salazar.cheers.core.data.util.Utils.conditional

@Preview
@Composable
fun CommentItemPreview() {
    CommentItem(
        comment = Comment(),
        onLike = { /*TODO*/ },
        onReply = { /*TODO*/ },
        onCommentClicked = { /*TODO*/ })
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    readOnly: Boolean = false,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onCommentClicked: () -> Unit,
    onLongClick: () -> Unit = {},
) {

    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    Row(
        modifier = modifier
            .fillMaxWidth()
            .conditional(readOnly) {
                background(secondaryContainer)
            }
            .combinedClickable(
                onClick = onCommentClicked,
                onLongClick = onLongClick,
            )
            .padding(padding),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = comment.avatar)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.padding(top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Username(
                        username = comment.username,
                        verified = comment.verified,
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = relativeTimeFormatter(epoch = comment.createTime),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium,
                )
                val replyCount = comment.replyCount
                if (comment.posting)
                    Text(
                        text = "Posting...",
                    )

                if (!readOnly) {
                    if (replyCount > 0)
                        TextButton(
                            onClick = { onReply() },
                            modifier = Modifier.offset(x = (-12).dp)
                        ) {
                            Text(
                                text = "$replyCount ${if (replyCount > 1) "replies" else "reply"}",
                            )
                        }
                    else if (comment.replyToCommentId == null)
                        TextButton(
                            onClick = { onReply() },
                            contentPadding = PaddingValues(horizontal = 0.dp)
                        ) {
                            Text(text = "Reply")
                        }
                }
            }
        }
        val isReply = comment.replyToCommentId != null

        if (!readOnly || isReply) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val icon = if (comment.hasLiked)
                    Icons.Default.Favorite
                else
                    Icons.Default.FavoriteBorder
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(ButtonDefaults.IconSize)
                        .noRippleClickable { onLike() }
                )
                AnimatedTextCounter(
                    targetState = comment.likeCount,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}