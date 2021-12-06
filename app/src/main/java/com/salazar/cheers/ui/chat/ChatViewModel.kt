package com.salazar.cheers.ui.chat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.internal.Message
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()
    val channelId = mutableStateOf("")

    fun messages(channelId: String) = FirestoreChat.getChatMessages(channelId = channelId)

    fun sendMessage(message: Message, channelId: String) =
        FirestoreChat.sendMessage(message, channelId)
}