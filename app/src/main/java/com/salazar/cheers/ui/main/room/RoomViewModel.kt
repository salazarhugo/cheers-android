package com.salazar.cheers.ui.main.room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.domain.models.ChatChannel
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
    val userRepository: UserRepository,
    val chatRepository: ChatRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RoomViewModelState(isLoading = true))
    private lateinit var roomId: String

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("roomId")?.let { roomId ->
            this.roomId = roomId
        }

        viewModelScope.launch {
            chatRepository.getChannel(channelId = roomId).collect { room ->
                onRoomChange(room = room)
            }
        }

        viewModelScope.launch {
            val result = chatRepository.getRoomMembers(roomId = roomId)
            when(result) {
                is com.salazar.cheers.data.Result.Success -> onMembersChange(members = result.data)
                is com.salazar.cheers.data.Result.Error -> updateError(message = result.message)
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
            chatRepository.leaveRoom(roomId = roomId)
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