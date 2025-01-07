package com.salazar.cheers.feature.chat.ui.screens.messages

import RoomsUIAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.feature.chat.ui.chats.ChatsMoreBottomSheet

@Composable
fun MessagesRoute(
    messagesViewModel: MessagesViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToChatCamera: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToChatWithChannelId: (String) -> Unit,
    navigateToNewChat: () -> Unit,
) {
    val uiState by messagesViewModel.uiState.collectAsStateWithLifecycle()
    var showChatMoreDialog by remember { mutableStateOf<ChatChannel?>(null) }

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
                    showChatMoreDialog = action.chat
                }
                is RoomsUIAction.OnSearchInputChange -> messagesViewModel.onSearchInputChange(action.query)
                is RoomsUIAction.OnUserClick -> navigateToOtherProfile(action.userId)
                is RoomsUIAction.OnRoomClick -> navigateToChatWithChannelId(action.roomId)
                is RoomsUIAction.OnFilterClick -> messagesViewModel.onFilterClick(action.filter)
                else -> {}
            }
        }
    )

    val chat = showChatMoreDialog

    if (chat != null) {
        ChatsMoreBottomSheet(
            chat = chat,
            modifier = Modifier,
            onDismissRequest = {
                showChatMoreDialog = null
            }
        )
    }
}