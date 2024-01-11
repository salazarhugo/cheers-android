package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MessagesRoute(
    messagesViewModel: MessagesViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToChatCamera: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToChatWithChannelId: (String) -> Unit,
    navigateToNewChat: () -> Unit,
    navigateToChatsMoreSheet: (String) -> Unit,
) {
    val uiState by messagesViewModel.uiState.collectAsStateWithLifecycle()

    MessagesScreen(
        uiState = uiState,
        onNewChatClicked = navigateToNewChat,
        onRoomsUIAction =  { action ->
            when(action) {
                RoomsUIAction.OnBackPressed -> navigateBack()
                is RoomsUIAction.OnPinRoom -> messagesViewModel.onRoomPin(action.roomId)
                RoomsUIAction.OnSwipeRefresh -> messagesViewModel.onSwipeRefresh()
                is RoomsUIAction.OnCameraClick -> navigateToChatCamera(action.id)
                is RoomsUIAction.OnRoomLongPress ->  {
                    if (action.roomId.isNotBlank())
                        navigateToChatsMoreSheet(action.roomId)
                }
                is RoomsUIAction.OnSearchInputChange -> messagesViewModel.onSearchInputChange(action.query)
                is RoomsUIAction.OnUserClick -> navigateToOtherProfile(action.userId)
                is RoomsUIAction.OnRoomClick -> navigateToChatWithChannelId(action.roomId)
            }
        }
    )
}