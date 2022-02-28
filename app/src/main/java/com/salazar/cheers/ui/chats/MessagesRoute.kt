package com.salazar.cheers.ui.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Messages screen.
 *
 * @param messagesViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MessagesRoute(
    messagesViewModel: MessagesViewModel,
    navActions: CheersNavigationActions,
    username: String,
    verified: Boolean,
) {
    val uiState by messagesViewModel.uiState.collectAsState()
    MessagesScreen(
        uiState = uiState,
        username = username,
        verified = verified,
        onBackPressed = {},
        onActivityIconClicked = {
        },
        onChannelClicked = { channelId,->
            navActions.navigateToChat(channelId)
        },
        onLongPress = { navActions.navigateToChatsMoreSheet(it) },
        onNewMessageClicked = {
        },
    )
}