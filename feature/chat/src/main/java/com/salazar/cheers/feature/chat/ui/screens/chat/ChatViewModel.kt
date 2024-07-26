package com.salazar.cheers.feature.chat.ui.screens.chat

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.feature.chat.data.GetChatChannelUseCase
import com.salazar.cheers.domain.seen_room.SeenRoomUseCase
import com.salazar.cheers.domain.send_message.SendMessageUseCase
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.chat.websocket.ChatEvent
import com.salazar.cheers.data.chat.websocket.ChatWebSocketManager
import com.salazar.cheers.domain.get_chat.GetChatFlowUseCase
import com.salazar.cheers.shared.util.Resource
import com.salazar.cheers.shared.util.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    statsHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val webSocketManager: ChatWebSocketManager,
    private val chatRepository: ChatRepository,
    private val seenRoomUseCase: SeenRoomUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatChannelUseCase: GetChatChannelUseCase,
    private val getChatFlowUseCase: GetChatFlowUseCase,
) : ViewModel() {

    lateinit var userID: String
    var hasChannel = true
    private var typingJob: Job? = null
    lateinit var chatID: String

    private val viewModelState = MutableStateFlow(ChatUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        val channelID = statsHandle.get<String>(CHANNEL_ID)

        if (channelID != null) {
            chatID = channelID
            loadChannel(channelID)
        } else {
            viewModelScope.launch {
                val userId = statsHandle.get<String>(USER_ID)!!
                userID = userId
                getChatChannelUseCase(userID).collect(::updateChatChannel)
            }
        }
    }

    private fun loadChannel(channelID: String) {
        viewModelScope.launch {
            getChatFlowUseCase(chatID = channelID).collect { channel ->
                updateChatChannel(channel)
                seenRoomUseCase(roomId = channelID)
            }
        }
    }

    private fun listenChatMessages(channelID: String) {
        viewModelScope.launch {
            chatRepository.listMessages(channelId = channelID).collect { messages ->
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
//                chatRepository.sendImage(channel.id, images)
            }
    }

    suspend fun createGroupChat(): Resource<String> {
//        val user = userRepository.getUserFlow(userID).first()
        val user = UserItem.newBuilder()
        return Resource.Error("")
//        return chatRepository.getOrCreateGroupChat(user.username, listOf(userID))
    }

    fun sendTextMessage(text: String) {
        // Reset Reply Message
        onReplyMessage(null)

        viewModelScope.launch {
            var channelId = uiState.value.channel?.id!!
            if (!hasChannel) {
                val result = createGroupChat()
                when (result) {
                    is Resource.Success -> {
//                        channelId = result.data
//                        loadChannel(result.data)
                    }

                    is Resource.Error -> {
                        updateErrorMessage(result.message)
                    }

                    else -> {}
                }
            }

            val result = sendMessageUseCase(roomId = channelId, text = text)
            when (result) {
                is Result.Error -> updateErrorMessage(result.error.name)
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
        chatID = chatChannel.id
        listenChatMessages(chatChannel.id)
    }

    fun unsendMessage(messageId: String) {
        // TODO Unsend message
    }

    fun onTextChanged(textState: TextFieldValue) {
        val previousText = viewModelState.value.textState.text
        viewModelState.update {
            it.copy(textState = textState)
        }
        val isTyping = textState.text.length > previousText.length

        if (isTyping.not())
            return

        if (typingJob?.isActive == true) {
            typingJob?.cancel()
            typingJob = viewModelScope.launch {
                delay(1000)
                webSocketManager.sendChatEvent(chatID, ChatEvent.EndTyping)
            }
        } else {
            typingJob = viewModelScope.launch {
                webSocketManager.sendChatEvent(chatID, ChatEvent.StartTyping)
                delay(1000)
                webSocketManager.sendChatEvent(chatID, ChatEvent.EndTyping)
            }
        }
    }

    fun likeMessage(messageId: String) {
        //TODO Like Message
    }

    fun unlikeMessage(messageId: String) {
        //TODO Unlike Message
    }

    fun sendReadReceipt() {
        if (!::chatID.isInitialized) {
            return
        }
        viewModelScope.launch {
            chatRepository.sendReadReceipt(chatID)
        }
    }


    fun startPresence() {
        if (!::chatID.isInitialized) {
            return
        }
        GlobalScope.launch {
            webSocketManager.sendChatEvent(chatID, ChatEvent.StartPresence)
        }
    }

    fun endPresence() {
        if (!::chatID.isInitialized) {
            return
        }
        GlobalScope.launch {
            webSocketManager.sendChatEvent(chatID, ChatEvent.EndPresence)
        }
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
//        val fileName = generateRecordingName("")
        val fileName = ""

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