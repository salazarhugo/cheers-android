package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.cheersUser
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.util.relativeTimeFormatter
import java.util.Date

@Composable
fun PostHeader(
    name: String,
    username: String,
    verified: Boolean,
    avatar: String,
    drinkName: String? = null,
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
                name = name,
                username = username,
                avatar = avatar,
                size = 33.dp,
                onClick = onUserClick,
            )
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                val trailingText = if (drinkName.isNullOrBlank()) {
                    null
                } else {
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                            append("is drinking ")
                        }
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(drinkName)
                        }
                    }
                }
                Username(
                    username = username,
                    verified = verified,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    color = color,
                    onClick = onUserClick,
                    trailingText = trailingText,
                    maxLines = 2,
                )
                if (!locationName.isNullOrBlank()) {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = relativeTimeFormatter(seconds = createTime),
                style = MaterialTheme.typography.labelMedium,
                color = color,
            )
            if (isPublic) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Icon(
                imageVector = Icons.Default.MoreHoriz,
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
    val user = cheersUser
    CheersPreview {
        PostHeader(
            username = user.username,
            verified = user.verified,
            avatar = user.picture.orEmpty(),
            locationName = "Dubai",
            createTime = Date().time / 1000,
            name = user.name,
        )
    }
}