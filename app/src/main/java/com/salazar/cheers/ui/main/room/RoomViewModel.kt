package com.salazar.cheers.ui.main.room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        val members: List<User> = emptyList(),
        override val isLoading: Boolean,
        override val errorMessage: String,
    ) : RoomUiState
}

data class RoomViewModelState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val members: List<User> = emptyList(),
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
    val userRepository: UserRepository,
    private val stateHandle: SavedStateHandle,
    val chatRepository: ChatRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RoomViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("roomId")?.let { roomId ->
            viewModelScope.launch {
                chatRepository.getChannel(channelId = roomId).collect { room ->
                    onRoomChange(room = room)
                    val members = userRepository.getUsersWithListOfIds(room.members)
                    onMembersChange(members)
                }
            }
        }
    }

    fun onLeaveRoom() {
        viewModelScope.launch {
            chatRepository.leaveRoom(channelId = viewModelState.value.room?.id!!)
        }
    }

    private fun onMembersChange(members: List<User>) {
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

