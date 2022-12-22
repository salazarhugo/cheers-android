package com.salazar.cheers.ui.main.chat

import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.Message
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.domain.usecase.seen_room.SeenRoomUseCase
import com.salazar.cheers.domain.usecase.send_message.SendMessageUseCase
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.internal.RoomStatus
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
    val textState: TextFieldValue = TextFieldValue(),
    val replyMessage: ChatMessage? = null,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    statsHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val seenRoomUseCase: SeenRoomUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
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

    private fun loadChannel(channelID: String) {
        viewModelScope.launch {
            chatRepository.getChannel(channelId = channelID).collect { channel ->
                updateChatChannel(channel)
                seenRoomUseCase(roomId = channelID)
            }
        }
        viewModelScope.launch {
            chatRepository.getMessages(channelId = channelID).collect { messages ->
                viewModelState.update {
                    it.copy(messages = messages, isLoading = false)
                }
            }
        }
    }

    fun sendImageMessage(images: List<Uri>) {
        val channel = uiState.value.channel
        if (channel != null)
        viewModelScope.launch {
            chatRepository.sendImage(channel.id, images)
        }
    }

    suspend fun createGroupChat(): Result<String> {
        val user = userRepository.getUserFlow(userID).first()
        return chatRepository.createGroupChat(user.username, listOf(userID))
    }

    fun sendTextMessage(text: String) {
        // Reset Reply Message
        onReplyMessage(null)

        viewModelScope.launch {
            var channelId = uiState.value.channel?.id!!
            if (!hasChannel) {
                val result = createGroupChat()
                when(result) {
                    is Result.Success -> {
                        channelId = result.data
                        loadChannel(result.data)
                    }
                    is Result.Error -> {
                        updateErrorMessage(result.message)
                    }
                }
            }

            val result = sendMessageUseCase(roomId = channelId, text = text)
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

    fun onTextChanged(textState: TextFieldValue) {
        viewModelState.update {
            it.copy(textState = textState)
        }

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

    fun onReplyMessage(message: ChatMessage?) {
        viewModelState.update {
            it.copy(replyMessage = message)
        }
        // Jump to bottom
        // Open keyboard
    }
}

sealed class ChatUIAction {
    object OnSwipeRefresh : ChatUIAction()
    data class OnLikeClick(val message: Message) : ChatUIAction()
    data class OnReplyMessage(val message: ChatMessage?) : ChatUIAction()
}
