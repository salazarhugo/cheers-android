package com.salazar.cheers.ui.chat

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.*
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.workers.UploadImageMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ChatUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoChannel(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ChatUiState

    data class HasChannel(
        val channel: ChatChannel,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ChatUiState
}

private data class ChatViewModelState(
    val channel: ChatChannel? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): ChatUiState =
        if (channel != null) {
            ChatUiState.HasChannel(
                channel = channel,
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

class ChatViewModel @AssistedInject constructor(
    application: Application,
    @Assisted private val channelId: String
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private val viewModelState = MutableStateFlow(ChatViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshChannel()
        refreshCurrentUser()
        seenLastMessage()
    }

    fun messages(channelId: String): Flow<List<Message>> =
        FirestoreChat.getChatMessages(channelId = channelId)

    private val user2 = mutableStateOf<User?>(null)

    private fun refreshCurrentUser() {
        viewModelScope.launch {
            when (val result = Neo4jUtil.getCurrentUser()) {
                is Result.Success -> user2.value = result.data
                is Result.Error -> Log.e("NEO4J", result.exception.toString())
            }
        }
    }

    private fun refreshChannel() {
        viewModelScope.launch {
            FirestoreChat.getChatChannel(channelId).collect { channel ->
                viewModelState.value = ChatViewModelState(channel = channel)
            }
        }
    }

    private fun seenLastMessage() {
        viewModelScope.launch {
            FirestoreChat.seenLastMessage(channelId)
        }
    }

    fun sendImageMessage(images: List<Uri>) {
        val user = user2.value ?: return

        val uploadImageMessageWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadImageMessage>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "CHANNEL_ID" to channelId,
                        "USERNAME" to user.username,
                        "FULL_NAME" to user.fullName,
                        "IMAGES_URI" to images.map { it.toString() }.toTypedArray(),
                        "PROFILE_PICTURE_PATH" to user.profilePictureUrl,
                    )
                )
            }
                .build()

        // Actually start the work
        workManager.enqueue(uploadImageMessageWorkRequest)
    }

    fun sendTextMessage(text: String, channelId: String) {
        val user = user2.value ?: return
        viewModelScope.launch {
            val textMessage =
                TextMessage().copy(
                    senderId = FirebaseAuth.getInstance().currentUser?.uid!!,
                    text = text,
                    senderName = user.fullName,
                    senderUsername = user.username,
                    chatChannelId = channelId,
                    senderProfilePictureUrl = user.profilePictureUrl,
                    type = MessageType.TEXT,
                )

            FirestoreChat.sendMessage(textMessage, channelId)
        }
    }

    fun unsendMessage(channelId: String, messageId: String) {
        viewModelScope.launch {
            FirestoreChat.unsendMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun likeMessage(channelId: String, messageId: String) {
        viewModelScope.launch {
            FirestoreChat.likeMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun unlikeMessage(channelId: String, messageId: String) {
        viewModelScope.launch {
            FirestoreChat.unlikeMessage(channelId = channelId, messageId = messageId)
        }
    }

    @AssistedFactory
    interface ChatViewModelFactory {
        fun create(channelId: String): ChatViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ChatViewModelFactory,
            channelId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(channelId = channelId) as T
            }
        }
    }
}