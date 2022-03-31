package com.salazar.cheers.ui.main.chats

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
) {
    val uiState by messagesViewModel.uiState.collectAsState()

    MessagesScreen(
        uiState = uiState,
        onSwipeRefresh = messagesViewModel::onSwipeRefresh,
        onActivityIconClicked = {
        },
        onChannelClicked = { channelId ->
            navActions.navigateToChat(channelId)
        },
        onLongPress = { name, channelId ->
            navActions.navigateToChatsMoreSheet(name, channelId)
        },
        onNewMessageClicked = {
        },
        onFollowClick = messagesViewModel::onFollowClick,
        onUserClick = { navActions.navigateToOtherProfile(it) },
    )
}