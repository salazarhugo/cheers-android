package com.salazar.cheers.feature.chat.ui.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

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
    val uiState by messagesViewModel.uiState.collectAsStateWithLifecycle()

    MessagesScreen(
        uiState = uiState,
        onNewChatClicked = {
            navActions.navigateToNewChat()
        },
        onRoomsUIAction =  { action ->
            when(action) {
                RoomsUIAction.OnBackPressed -> navActions.navigateBack()
                is RoomsUIAction.OnPinRoom -> messagesViewModel.onRoomPin(action.roomId)
                RoomsUIAction.OnSwipeRefresh -> messagesViewModel.onSwipeRefresh()
                is RoomsUIAction.OnCameraClick -> navActions.navigateToChatCamera(action.id)
                is RoomsUIAction.OnRoomLongPress ->  {
                    if (action.roomId.isNotBlank())
                        navActions.navigateToChatsMoreSheet(action.roomId)
                }
                is RoomsUIAction.OnSearchInputChange -> messagesViewModel.onSearchInputChange(action.query)
                is RoomsUIAction.OnUserClick -> navActions.navigateToOtherProfile(action.userId)
                is RoomsUIAction.OnRoomClick -> navActions.navigateToChatWithChannelId(action.roomId)
            }
        }
    )
}