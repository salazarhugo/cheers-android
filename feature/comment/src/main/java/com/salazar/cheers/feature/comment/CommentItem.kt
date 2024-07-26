package com.salazar.cheers.feature.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.AnimatedTextCounter
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.util.Utils.conditional
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import java.util.Date

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    readOnly: Boolean = false,
    onLike: () -> Unit = {},
    onReply: () -> Unit = {},
    onCommentClicked: () -> Unit = {},
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
            AvatarComponent(
                modifier = Modifier.padding(top = 4.dp),
                avatar = comment.avatar,
                size = 36.dp
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.padding(top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
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
                        text = com.salazar.cheers.core.util.relativeTimeFormatter(seconds = comment.createTime),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )

                val replyCount = comment.replyCount

                if (comment.posting) {
                    Text(
                        text = "Posting...",
                    )
                }

                if (!readOnly && comment.replyToCommentId == null) {
                    val text = when(replyCount > 0) {
                        true ->  "$replyCount ${if (replyCount > 1) "replies" else "reply"}"
                        false -> "Reply"
                    }
                    TextButton(
                        onClick = { onReply() },
                        modifier = Modifier.offset(x = (-12).dp, y = -(12).dp)
                    ) {
                        Text(
                            text = text,
                            fontSize = 14.sp,
                        )
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

@ComponentPreviews
@Composable
fun CommentItemPreview() {
    CheersPreview {
        CommentItem(
            comment = Comment(
                username = "cheers",
                verified = true,
                hasLiked = true,
                text = "this is my very interesting comment!",
                createTime = Date().time / 1000 - 3600 * 4
            ),
        )
    }
}

