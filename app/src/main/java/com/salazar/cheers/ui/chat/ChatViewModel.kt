package com.salazar.cheers.ui.chat

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.ImageMessage
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.internal.TextMessage
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.Neo4jUtil
import com.salazar.cheers.workers.UploadImageMessage
import com.salazar.cheers.workers.UploadPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    val channelId = mutableStateOf("")

    fun messages(channelId: String) = FirestoreChat.getChatMessages(channelId = channelId)

    private val user2 = mutableStateOf<User?>(null)

    init {
        viewModelScope.launch {
            when (val result = Neo4jUtil.getCurrentUser()) {
                is Result.Success -> user2.value = result.data
                is Result.Error -> Log.e("NEO4J", result.exception.toString())
            }
        }
    }

    fun seenLastMessage(channelId: String) {
        viewModelScope.launch {
            FirestoreChat.seenLastMessage(channelId)
        }
    }

    fun sendImageMessage(images: List<Uri>, channelId: String) {
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
                        "PROFILE_PICTURE_PATH" to user.profilePicturePath,
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
                    senderProfilePicturePath = user.profilePicturePath,
                    type = MessageType.TEXT,
                    recipientId = ""
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
}