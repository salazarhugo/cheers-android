package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_chat_members.ListChatMembersUseCase
import com.salazar.cheers.shared.util.result.Result.Error
import com.salazar.cheers.shared.util.result.Result.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RoomUiState {
    val isLoading: Boolean
    val errorMessage: String

    data class NoRoom(
        override val isLoading: Boolean,
        override val errorMessage: String,
    ) : RoomUiState

    data class HasRoom(
        val room: ChatChannel,
        val members: List<UserItem> = emptyList(),
        override val isLoading: Boolean,
        override val errorMessage: String,
    ) : RoomUiState
}

data class RoomViewModelState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val members: List<UserItem> = emptyList(),
    val room: ChatChannel? = null,
) {
    fun toUiState(): RoomUiState =
        if (room == null) {
            RoomUiState.NoRoom(
                isLoading = isLoading,
                errorMessage = errorMessage,
            )
        } else {
            RoomUiState.HasRoom(
                room = room,
                members = members,
                isLoading = isLoading,
                errorMessage = errorMessage,
            )
        }
}

@HiltViewModel
class RoomViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    val chatRepository: com.salazar.cheers.data.chat.repository.ChatRepository,
    val listChatMembersUseCase: ListChatMembersUseCase,
) : ViewModel() {
    private val args = stateHandle.toRoute<ChatInfoScreen>()

    private val viewModelState = MutableStateFlow(RoomViewModelState(isLoading = true))
    private val chatID = args.chatID

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            chatRepository.getChannel(channelId = chatID).collect { room ->
                onRoomChange(room = room)
            }
        }

        viewModelScope.launch {
            when (val result = listChatMembersUseCase(chatID)) {
                is Success -> {
                    onMembersChange(members = result.data)
                }

                is Error -> {
                    updateError(message = result.error.name)
                }
            }
        }
    }

    fun updateError(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    fun onLeaveRoom() {
        viewModelScope.launch {
            chatRepository.leaveRoom(roomId = chatID)
        }
    }

    private fun onMembersChange(members: List<UserItem>) {
        viewModelState.update {
            it.copy(members = members)
        }
    }

    private fun onRoomChange(room: ChatChannel?) {
        viewModelState.update {
            it.copy(room = room)
        }
    }
}