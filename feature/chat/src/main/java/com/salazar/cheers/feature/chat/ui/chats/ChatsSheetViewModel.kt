package com.salazar.cheers.feature.chat.ui.chats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ChatChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatsSheetUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val room: ChatChannel? = null,
)

@HiltViewModel
class ChatsSheetViewModel @Inject constructor(
    private val chatRepository: com.salazar.cheers.data.chat.repository.ChatRepository,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ChatsSheetUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )


    fun leaveChannel(chatID: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            chatRepository.leaveRoom(roomId = chatID)
            onComplete()
        }
    }

    fun deleteChats(chatID: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            chatRepository.deleteChats(channelId = chatID)
            onComplete()
        }
    }

    fun deleteChannel(chatID: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            chatRepository.deleteRoom(channelId = chatID)
            onComplete()
        }
    }
}
