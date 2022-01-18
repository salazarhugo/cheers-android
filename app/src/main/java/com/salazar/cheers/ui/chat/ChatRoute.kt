package com.salazar.cheers.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.CheersNavigationActions
import com.salazar.cheers.components.LoadingScreen

/**
 * Stateful composable that displays the Navigation route for the Chat screen.
 *
 * @param chatViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ChatRoute(
    chatViewModel: ChatViewModel,
    navActions: CheersNavigationActions,
    username: String,
    name: String,
    profilePictureUrl: String,
) {
    val uiState by chatViewModel.uiState.collectAsState()

    when(uiState) {
        is ChatUiState.HasChannel -> {
            ChatScreen(
                uiState = uiState as ChatUiState.HasChannel,
                onTitleClick = {},
                onPoBackStack = { navActions.navigateBack() },
                onUnlike = chatViewModel::unlikeMessage,
                onLike = chatViewModel::likeMessage,
                onUnsendMessage = chatViewModel::unsendMessage,
                onMessageSent = chatViewModel::sendTextMessage,
                onImageSelectorClick = {},
                onCopyText = {},
                username = username,
                name = name,
                profilePicturePath = profilePictureUrl,
            )
        }
        is ChatUiState.NoChannel -> {
            LoadingScreen()
        }
    }
}