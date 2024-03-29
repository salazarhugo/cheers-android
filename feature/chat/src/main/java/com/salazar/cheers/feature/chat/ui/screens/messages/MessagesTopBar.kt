package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
    Toolbar(
        modifier = modifier,
        title = "Chat",
        onBackPressed = onBackPressed,
        actions = {
            Text(
                text = websocketState.toString()
            )
            IconButton(onClick = onNewChatClicked) {
                Icon(Icons.Default.Add, null)
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