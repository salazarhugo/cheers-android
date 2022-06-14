package com.salazar.cheers.ui.main.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Messages screen.
 *
 * @param messagesViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun MessagesRoute(
    messagesViewModel: MessagesViewModel = hiltViewModel(),
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
        onLongPress = { channelId ->
            if (channelId.isNotBlank())
                navActions.navigateToChatsMoreSheet(channelId)
        },
        onNewChatClicked = {
            navActions.navigateToNewChat()
        },
        onFollowToggle = messagesViewModel::onFollowToggle,
        onUserClick = { navActions.navigateToOtherProfile(it) },
        onCameraClick = { navActions.navigateToChatCamera(it) },
    )
}