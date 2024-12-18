package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.data.chat.websocket.WebsocketState


@Composable
fun MessagesTopBar(
    websocketState: WebsocketState?,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onNewChatClicked: () -> Unit = {},
) {
    val title = when (websocketState) {
        WebsocketState.Loading -> "Connecting..."
        else -> "Chat"
    }

    Toolbar(
        modifier = modifier,
        title = title,
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = onNewChatClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New chat icon",
                )
            }
        },
    )
}

@Preview
@Composable
private fun MessagesTopBarPreview() {
    CheersPreview {
        MessagesTopBar(
            websocketState = WebsocketState.Loading,
        )
    }
}