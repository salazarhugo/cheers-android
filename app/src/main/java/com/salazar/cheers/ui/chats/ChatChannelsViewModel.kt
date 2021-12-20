package com.salazar.cheers.ui.chats

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelType
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChatChannelUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val searchInput: String

    data class NoChannels(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : ChatChannelUiState

    data class HasChannels(
        val channels: List<ChatChannel>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : ChatChannelUiState
}

private data class ChatChannelsViewModelState(
    val channels: List<ChatChannel>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): ChatChannelUiState =
        if (channels == null) {
            ChatChannelUiState.NoChannels(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            ChatChannelUiState.HasChannels(
                channels = channels,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class ChatChannelsViewModel @Inject constructor() : ViewModel() {

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    val name = mutableStateOf("")

    private val viewModelState = MutableStateFlow(ChatChannelsViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshChatChannels()
    }

    private fun refreshChatChannels() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            FirestoreChat.getChatChannels().collect { channels ->
                a(channels.filter { it.type == ChatChannelType.DIRECT })
            }
        }
    }

    private fun a(directChannels: List<ChatChannel>) {
        viewModelScope.launch {
            val channels = mutableListOf<ChatChannel>()

            directChannels.forEach {
                val otherUserId = it.members.find { it != FirebaseAuth.getInstance().currentUser?.uid!! } ?: return@forEach

                when (val result = Neo4jUtil.getUser(otherUserId)) {
                    is Result.Success ->  channels.add(it.copy(otherUser = result.data))
                    is Result.Error -> {}
                }
            }

            viewModelState.update {
                it.copy(channels = channels, isLoading = false)
            }
        }
    }

}