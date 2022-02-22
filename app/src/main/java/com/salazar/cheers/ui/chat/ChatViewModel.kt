package com.salazar.cheers.ui.chat

import android.app.Activity
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.UserRepository
import com.salazar.cheers.internal.*
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.workers.UploadImageMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
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
        val messages: List<Message>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ChatUiState
}

private data class ChatViewModelState(
    val channel: ChatChannel? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val messages: List<Message> = emptyList(),
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

class ChatViewModel @AssistedInject constructor(
    application: Application,
    private val userRepository: UserRepository,
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
        refreshCurrentUser()
        refreshChannel()
        seenLastMessage()

        viewModelScope.launch {
            FirestoreChat.getChatMessages(channelId).collect { messages ->
                viewModelState.update { it.copy(messages = messages) }
            }
        }
    }

    private val user2 = mutableStateOf<User?>(null)

    private fun refreshChannel() {
        viewModelScope.launch {
            FirestoreChat.getChatChannel(channelId).collect { channel ->
                if (channel.type == ChatChannelType.DIRECT) {
                    val otherUserId =
                        channel.members.lastOrNull { it != FirebaseAuth.getInstance().currentUser?.uid }
                            ?: channel.createdBy
                    refreshOtherUser(otherUserId = otherUserId)
                }
                viewModelState.update { it.copy(channel = channel) }
            }
        }
    }

    private fun refreshOtherUser(otherUserId: String) {
        viewModelScope.launch {
            viewModelState.update {
                val otherUser = userRepository.getUser(otherUserId)
                it.copy(channel = it.channel?.copy(otherUser = otherUser))
            }
        }
    }

    private fun refreshCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            user2.value = user
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

    fun sendTextMessage(text: String) {
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

    fun unsendMessage(messageId: String) {
        viewModelScope.launch {
            FirestoreChat.unsendMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun likeMessage(messageId: String) {
        viewModelScope.launch {
            FirestoreChat.likeMessage(channelId = channelId, messageId = messageId)
        }
    }

    fun unlikeMessage(messageId: String) {
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

@Composable
fun chatViewModel(channelId: String): ChatViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).chatViewModelFactory()

    return viewModel(factory = ChatViewModel.provideFactory(factory, channelId = channelId))
}
