package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import java.util.Date

@Composable
fun PostHeader(
    username: String,
    verified: Boolean,
    avatar: String,
    modifier: Modifier = Modifier,
    createTime: Long = Date().time / 1000,
    locationName: String? = null,
    darkMode: Boolean = false,
    isPublic: Boolean = false,
    onUserClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    val color = if (darkMode) Color.White else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarComponent(
                avatar = avatar,
                size = 33.dp,
                onClick = onUserClick,
            )
            Spacer(Modifier.width(8.dp))
            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Username(
                        username = username,
                        verified = verified,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        color = color,
                        onClick = onUserClick,
                    )
                }
                if (!locationName.isNullOrBlank()) {
                    Text(text = locationName, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = com.salazar.cheers.core.util.relativeTimeFormatter(seconds = createTime),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 8.dp),
                color = color,
            )
            if (isPublic)
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
                    .clickable { onMoreClick() },
                tint = color
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun PostHeaderPreview() {
    CheersPreview {
        PostHeader(
            username = "cheers",
            verified = true,
            avatar = "",
            locationName = "Dubai",
            createTime = Date().time / 1000,
        )
    }
}