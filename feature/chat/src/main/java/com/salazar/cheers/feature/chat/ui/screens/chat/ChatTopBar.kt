
package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatType
import com.salazar.cheers.feature.chat.ui.components.DirectChatBar
import com.salazar.cheers.feature.chat.ui.components.GroupChatBar


@Composable
fun ChatTopBar(
    chatChannel: ChatChannel,
    scrollBehavior: TopAppBarScrollBehavior,
    onChatUIAction: (ChatUIAction) -> Unit,
) {
    when (chatChannel.type) {
        ChatType.DIRECT -> {
            DirectChatBar(
                name = chatChannel.name,
                verified = chatChannel.verified,
                picture = chatChannel.picture,
                isTyping = chatChannel.isOtherUserTyping,
                onNavIconPressed = {
                    onChatUIAction(ChatUIAction.OnBackPressed)
                },
                onTitleClick = {
                    onChatUIAction(ChatUIAction.OnUserClick(chatChannel.otherUserId))
                },
                scrollBehavior = scrollBehavior,
                onInfoClick = {
                    onChatUIAction(ChatUIAction.OnRoomInfoClick(chatChannel.id))
                },
            )
        }

        ChatType.GROUP -> {
            GroupChatBar(
                name = chatChannel.name,
                membersCount = chatChannel.membersCount,
                picture = chatChannel.picture,
                onNavIconPressed = {
                    onChatUIAction(ChatUIAction.OnBackPressed)
                },
                onTitleClick = {},
                onInfoClick = {
                    onChatUIAction(ChatUIAction.OnRoomInfoClick(chatChannel.id))
                },
                scrollBehavior = scrollBehavior,
            )
        }

        else -> Unit
    }
}

