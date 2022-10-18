package com.salazar.cheers.ui.main.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.Message
import cheers.chat.v1.RoomType
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
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

    lateinit var userID: String
    var hasChannel = true
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

        if (channelID != null)
            loadChannel(channelID)
        else {
            val userId = statsHandle.get<String>("userID")!!
            runBlocking {
                userID = userId
                val channel = chatRepository.getChatWithUser(userId)
                updateChatChannel(channel)
                if (channel.id != "temp")
                    loadChannel(channel.id)
                else
                    hasChannel = false
            }
        }
    }

    fun loadChannel(channelID: String) {
        viewModelScope.launch {
            chatRepository.getChannel(channelId = channelID).collect { channel ->
                updateChatChannel(channel)
            }
        }

        viewModelScope.launch {
            chatRepository.getMessages(channelId = channelID).collect { messages ->
                viewModelState.update {
                    it.copy(messages = messages, isLoading = false)
                }
            }
        }

        viewModelScope.launch {
            chatRepository.joinChannel(channelId = channelID)
        }
    }

    fun sendImageMessage(images: List<Uri>) {
        val channel = uiState.value.channel
        if (channel != null)
        viewModelScope.launch {
            chatRepository.sendImage(channel.id, images)
        }
    }

    fun sendTextMessage(text: String) {
        viewModelScope.launch {
            var channelId = uiState.value.channel?.id!!
            if (!hasChannel) {
                val user = userRepository.getUserFlow(userID).first()
                val roomId = chatRepository.createGroupChat(user.username, listOf(userID), user)
                channelId = roomId
                loadChannel(roomId)
            }
            val result = chatRepository.sendMessage(channelId = channelId, text)
            when (result) {
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

    fun updateChatChannel(chatChannel: ChatChannel) {
        viewModelState.update {
            it.copy(channel = chatChannel)
        }
    }

    fun unsendMessage(messageId: String) {
        viewModelScope.launch {
//            FirestoreChat.unsendMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun onTextChanged() {
        return
        val channel = uiState.value.channel

        if (typingJob?.isCompleted == false) {
            typingJob = viewModelScope.launch {
                delay(2000L)
            }
            return
        }


//        typingJob = viewModelScope.launch {
//            chatRepository.startTyping(channelId = channelId)
//            delay(2000L)
//        }
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
