package com.salazar.cheers.ui.main.chat

import android.app.Application
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.workers.UploadImageMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChatUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoChannel(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ChatUiState

    data class HasChannel(
        val channel: ChatChannel,
        val messages: List<ChatMessage>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ChatUiState
}

private data class ChatViewModelState(
    val channel: ChatChannel? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
) {
    fun toUiState(): ChatUiState =
        if (channel != null) {
            ChatUiState.HasChannel(
                channel = channel,
                messages = messages,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            ChatUiState.NoChannel(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    statsHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ChatViewModelState(isLoading = false))
    private var channelId = ""
    private var typingJob: Job? = null

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        statsHandle.get<String>("channelId")?.let {
            channelId = it
        }

        viewModelScope.launch {
            chatRepository.joinChannel(channelId = channelId)
        }

        viewModelScope.launch {
            chatRepository.getChannel(channelId = channelId).collect { channel ->
                viewModelState.update {
                    it.copy(channel = channel)
                }
            }
        }

        viewModelScope.launch {
            chatRepository.getMessages(channelId = channelId).collect { messages ->
                viewModelState.update {
                    it.copy(messages = messages, isLoading = false)
                }
            }
        }
    }

    fun sendImageMessage(images: List<Uri>) {
        viewModelScope.launch {
            chatRepository.sendImage(channelId, images)
        }
    }

    fun sendTextMessage(text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(channelId = channelId, text)
        }
    }

    fun unsendMessage(messageId: String) {
        viewModelScope.launch {
//            FirestoreChat.unsendMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun onTextChanged() {
        if (typingJob?.isCompleted == false) {
            typingJob = viewModelScope.launch {
                delay(2000L)
            }
            return
        }

        typingJob = viewModelScope.launch {
            chatRepository.startTyping(channelId = channelId)
            delay(2000L)
        }
    }

    fun likeMessage(messageId: String) {
        viewModelScope.launch {
//            FirestoreChat.likeMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun unlikeMessage(messageId: String) {
        viewModelScope.launch {
//            FirestoreChat.unlikeMessage(channelId = channelId, messageId = messageId)
        }
    }
}
