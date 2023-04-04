package com.salazar.cheers.chat.ui.screens.chat

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.core.data.Result
import com.salazar.cheers.data.models.generateRecordingName
import com.salazar.cheers.chat.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.chat.domain.usecase.seen_room.SeenRoomUseCase
import com.salazar.cheers.chat.domain.usecase.send_message.SendMessageUseCase
import com.salazar.cheers.chat.domain.models.ChatChannel
import com.salazar.cheers.chat.domain.models.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val channel: ChatChannel? = null,
    val messages: List<ChatMessage> = emptyList(),
    val textState: TextFieldValue = TextFieldValue(),
    val replyMessage: ChatMessage? = null,
    val isRecording: Boolean = false,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    statsHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
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
                when (result) {
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
        // TODO Unsend message
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

        typingJob = viewModelScope.launch {
            // TODO Start Typing
            delay(2000L)
        }
    }

    fun likeMessage(messageId: String) {
        //TODO Like Message
    }

    fun unlikeMessage(messageId: String) {
        //TODO Unlike Message
    }

    fun onReplyMessage(message: ChatMessage?) {
        viewModelState.update {
            it.copy(replyMessage = message)
        }
        // Jump to bottom
        // Open keyboard
    }

    fun updateIsRecording(isRecording: Boolean) {
        viewModelState.update {
            it.copy(isRecording = isRecording)
        }
    }


    var recorder: MediaRecorder? = null

    fun startRecording() {
        val fileName = generateRecordingName("")


        val path = context.externalCacheDir?.absolutePath + fileName
        val dir = File(path)
        if (!dir.exists())
            dir.createNewFile()

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(path)

            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setAudioEncodingBitRate(16 * 44100)
            setAudioSamplingRate(44100)
        }

        try {
            recorder?.prepare()
            recorder?.start()
            updateIsRecording(true)
        } catch (e: IOException) {
            updateIsRecording(false)
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
        updateIsRecording(false)
//        readRecordings()
    }
}

sealed class ChatUIAction {
    object OnBackPressed : ChatUIAction()
    object OnSwipeRefresh : ChatUIAction()
    object OnImageSelectorClick : ChatUIAction()
    data class OnTextInputChange(val text: TextFieldValue) : ChatUIAction()
    data class OnCopyText(val text: String) : ChatUIAction()
    data class OnRoomInfoClick(val roomId: String) : ChatUIAction()
    data class OnUserClick(val userId: String) : ChatUIAction()
    data class OnLikeClick(val messageId: String) : ChatUIAction()
    data class OnUnLikeClick(val messageId: String) : ChatUIAction()
    data class OnReplyMessage(val message: ChatMessage?) : ChatUIAction()
    data class OnUnSendMessage(val messageId: String) : ChatUIAction()
    data class OnSendTextMessage(val text: String) : ChatUIAction()
}
