package com.salazar.cheers.feature.chat.ui.components.chat_item

import RoomsUIAction
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.util.relativeTimeFormatterMilli
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatStatus

@Composable
fun DirectChatComponent(
    channel: ChatChannel,
    modifier: Modifier = Modifier,
    onRoomsUIAction: (RoomsUIAction) -> Unit = {},
) {
    val backgroundColor = if (channel.pinned)
        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    else
        MaterialTheme.colorScheme.background

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(
                onClick = { onRoomsUIAction(RoomsUIAction.OnRoomClick(channel.id)) },
                onLongClick = {
                    onRoomsUIAction(RoomsUIAction.OnRoomLongPress(channel))
                }
            )
            .padding(16.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarComponent(
                avatar = channel.picture,
                size = 50.dp,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Username(
                    username = channel.name,
                    verified = channel.verified
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ChatStatus(
                        status = channel.status,
                        isOtherUserTyping = channel.isOtherUserTyping,
                        messageType = channel.lastMessageType,
                    )
                    if (channel.status != ChatStatus.EMPTY) {
                        val subtitle = buildAnnotatedString {
                            append("  •  ")
                            append(relativeTimeFormatterMilli(milliSeconds = channel.lastMessageTime))
                        }
                        val fontWeight = when (channel.status) {
                            ChatStatus.NEW -> FontWeight.Bold
                            else -> FontWeight.Normal
                        }

                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = fontWeight,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        val tint = MaterialTheme.colorScheme.outline

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (channel.status == ChatStatus.NEW) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BlueCheers)
                )
                Spacer(Modifier.width(12.dp))
            }

            val icon =
                if (channel.pinned)
                    Icons.Outlined.PinDrop
                else if (channel.status == ChatStatus.NEW)
                    Icons.Outlined.Sms
                else
                    Icons.Outlined.PhotoCamera

//            if (channel.unreadCount > 0) {
//                Box(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                        .clip(CircleShape)
//                        .background(BlueCheers),
//                    contentAlignment = Alignment.Center,
//                ) {
//                    AnimatedTextCounter(
//                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
//                        targetState = channel.unreadCount,
//                    )
//                }
//            }

            IconButton(
                onClick = { onRoomsUIAction(RoomsUIAction.OnCameraClick(channel.id)) },
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Camera Icon",
                    tint = tint,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun DirectChatItemPreview() {
    CheersPreview {
        DirectChatComponent(
            channel = ChatChannel(),
            modifier = Modifier,
        )
    }
}