package com.salazar.cheers.post.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.core.ui.ui.UserProfilePicture

@Composable
fun PostHeader(
    post: Post,
    public: Boolean,
    darkMode: Boolean = false,
    onHeaderClicked: (username: String) -> Unit = {},
    onMoreClicked: () -> Unit = {},
) {
    val color = if (darkMode) Color.White else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClicked(post.username) }
            .padding(16.dp, 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserProfilePicture(
                picture = post.profilePictureUrl,
                storyState = StoryState.EMPTY,
                size = 33.dp,
            )
            Spacer(Modifier.width(8.dp))
            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.salazar.cheers.core.share.ui.Username(
                        username = post.username,
                        verified = post.verified,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        color = color,
                    )
                    if (post.beverage.isNotBlank()) {
                        Text(
                            text = " is drinking ${post.beverage.lowercase()}",
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (post.locationName.isNotBlank())
                    Text(text = post.locationName, style = MaterialTheme.typography.labelSmall)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = com.salazar.cheers.core.util.relativeTimeFormatter(epoch = post.createTime),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 8.dp),
                color = color,
            )
            if (public)
                Icon(
                    Icons.Default.Public,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            Icon(
                Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onMoreClicked() },
                tint = color
            )
        }
    }
}
