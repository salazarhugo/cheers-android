package com.salazar.cheers.ui.chat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.internal.TextMessage
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

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
                    type = MessageType.TEXT
                )

            FirestoreChat.sendMessage(textMessage, channelId)
        }
    }
}