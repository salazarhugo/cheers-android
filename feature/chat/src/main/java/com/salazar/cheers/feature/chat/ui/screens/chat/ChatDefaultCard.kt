package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.ui.Username

@Composable
fun ChatDefaultCard(
    chatChannel: ChatChannel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarComponent(
            name = chatChannel.name,
            avatar = chatChannel.picture,
            modifier = Modifier.padding(3.dp),
            size = 110.dp,
        )
        Username(
            username = chatChannel.name,
            verified = chatChannel.verified,
        )
    }
}

@ComponentPreviews
@Composable
private fun ChatDefaultUserCase() {
    CheersPreview {
        ChatDefaultCard(
            chatChannel = ChatChannel(name = "cheers"),
            modifier = Modifier.padding(16.dp),
            onClick = {}
        )
    }
}