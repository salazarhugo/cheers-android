package com.salazar.cheers.feature.chat.ui.components.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.EmptyChatMessage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.feature.chat.ui.screens.chat.ChatUIAction


@Composable
fun BottomBarReply(
    message: ChatMessage?,
    onChatUIAction: (ChatUIAction) -> Unit,
) {
    if (message == null) return

    val nameOrUsername = message.senderName.ifBlank { message.senderUsername }

    Surface(tonalElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                AvatarComponent(
                    avatar = message.photoUrl,
                    name = message.senderName,
                    username = message.senderUsername,
                    modifier = Modifier
                        .padding(start = 8.dp),
                    size = 36.dp,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    if (nameOrUsername.isNotBlank()) {
                        Text(
                            text = nameOrUsername,
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = message.text.ifBlank { "Photo" }.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(
                onClick = {
                    onChatUIAction(ChatUIAction.OnReplyMessage(null))
                },
                modifier = Modifier
                    .padding(start = 32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun BottomBarReply() {
    CheersPreview {
        BottomBarReply(
            EmptyChatMessage.copy(text = "salut, tu vas bien?")
        ) { }
    }
}