package com.salazar.cheers.feature.chat.ui.chats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent

@Composable
fun EmptyChatsMessage(
    modifier: Modifier = Modifier,
) {
    MessageScreenComponent(
        modifier = modifier.padding(16.dp),
        title = "No messages yet",
        subtitle = "Looks like you haven't initiated a conversation with any party buddies",
    )
}


@ComponentPreviews
@Composable
fun EmptyChatsMessagePreview() {
    CheersPreview {
        EmptyChatsMessage(
            modifier = Modifier,
        )
    }
}