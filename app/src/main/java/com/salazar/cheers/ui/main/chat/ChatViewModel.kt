package com.salazar.cheers.ui.main.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.Message
import cheers.chat.v1.RoomType
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.internal.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val channel: ChatChannel? = null,
    val messages: List<ChatMessage> = emptyList(),
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    statsHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    lateinit var channelId: String
    lateinit var userID: String

    private var typingJob: Job? = null

    private val viewModelState = MutableStateFlow(ChatUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        val channelID = statsHandle.get<String>("channelId")

        if (channelID == null) {
            val userID = statsHandle.get<String>("userID")!!
            runBlocking {
                val user = userRepository.getUserFlow(userID).first()
                val channelID = chatRepository.createGroupChat(user.username, listOf(), user)
                channelId = channelID
            }
        }
        else {
            channelId = channelID
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
            val result = chatRepository.sendMessage(channelId = channelId, text)
            when(result) {
                is Resource.Error -> updateErrorMessage(result.message)
                else -> {}
            }
        }
    }

    private fun updateErrorMessage(message: String?) {
        viewModelState.update {
            it.copy(errorMessage = message)
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

sealed class ChatUIAction {
    object OnSwipeRefresh : ChatUIAction()
    data class OnLikeClick(val message: Message) : ChatUIAction()
}
