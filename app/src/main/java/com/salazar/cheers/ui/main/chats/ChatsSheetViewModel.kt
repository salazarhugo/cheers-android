package com.salazar.cheers.ui.main.chats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.domain.models.ChatChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatsSheetUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val room: ChatChannel? = null,
)

@HiltViewModel
class ChatsSheetViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ChatsSheetUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    lateinit var channelId: String

    init {
        stateHandle.get<String>("channelId")?.let { roomId ->
            channelId = roomId

            viewModelScope.launch {
                chatRepository.getChannel(roomId).collect { room ->
                    viewModelState.update {
                        it.copy(room = room)
                    }
                }
            }
        }
    }

    fun leaveChannel(onComplete: () -> Unit) {
        viewModelScope.launch {
            chatRepository.leaveRoom(roomId = channelId)
            onComplete()
        }
    }

    fun deleteChats(onComplete: () -> Unit) {
        viewModelScope.launch {
            chatRepository.deleteChats(channelId = channelId)
            onComplete()
        }
    }

    fun deleteChannel(onComplete: () -> Unit) {
        viewModelScope.launch {
            chatRepository.deleteRoom(channelId = channelId)
            onComplete()
        }
    }
}
