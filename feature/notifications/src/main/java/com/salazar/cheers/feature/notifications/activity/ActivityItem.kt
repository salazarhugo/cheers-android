package com.salazar.cheers.feature.notifications.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.core.model.followActivity
import com.salazar.cheers.core.model.infoActivity
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.messageFormatter
import com.salazar.cheers.core.util.relativeTimeFormatter


@Composable
internal fun ActivityItem(
    activity: Activity,
    modifier: Modifier = Modifier,
    onActivityClick: (Activity) -> Unit = {},
    onActivityUIAction: (ActivityUIAction) -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable { onActivityClick(activity) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (activity.type) {
                ActivityType.INFORMATION -> {
                    Icon(
                        modifier = Modifier
                            .size(46.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            .padding(8.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                    )
                }

                else -> {
                    AvatarComponent(
                        avatar = activity.avatar,
                        size = 46.dp,
                        onClick = {
                            onActivityUIAction(ActivityUIAction.OnUserClick(userId = activity.username))
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    val text = messageFormatter(text = activity.text, primary = true)
                    append(text)
                }
                append(" ")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ), fontWeight = FontWeight.Normal
                    )
                ) {
                    append(relativeTimeFormatter(seconds = activity.createTime))
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                softWrap = true,
            )
        }
        if (activity.type == ActivityType.FRIEND_ADDED)
            FriendButton(
                modifier = Modifier.padding(start = 16.dp),
                isFriend = true,
                onClick = {},
            )
        if (activity.photoUrl.isNotBlank()) {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onActivityUIAction(ActivityUIAction.OnPostClick(activity.mediaId)) }
                    .size(50.dp),
                painter = rememberAsyncImagePainter(model = activity.photoUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
private fun ActivityItemPreview() {
    CheersPreview {
        ActivityItem(
            activity = followActivity,
            onActivityClick = {},
            onActivityUIAction = {},
        )
        ActivityItem(
            activity = infoActivity,
            onActivityClick = {},
            onActivityUIAction = {},
        )
    }
}