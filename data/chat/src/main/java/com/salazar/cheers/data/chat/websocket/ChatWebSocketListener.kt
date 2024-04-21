package com.salazar.cheers.data.chat.websocket

import android.util.Log
import com.google.gson.Gson
import com.salazar.cheers.core.db.dao.ChatDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.MessageType
import com.salazar.cheers.core.model.ChatStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class ChatWebSocketListener @Inject constructor(
    private val chatDao: com.salazar.cheers.core.db.dao.ChatDao,
) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d("WEBSOCKET MESSAGE", "WEBSOCKET OPEN")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d(TAG, text)

        val message = Gson().fromJson(text, ChatMessage::class.java)
        Log.d(TAG, message.toString())

        message?.let {
            GlobalScope.launch {
                chatDao.insertMessage(
                    message.copy(
                        likedBy = emptyList(),
                        seenBy = emptyList(),
                        photoUrl = "",
                        senderUsername = "",
                        senderName = "",
                        senderProfilePictureUrl = "",
                        type = MessageType.TEXT,
                        status = ChatMessageStatus.DELIVERED,
                    ).asEntity()
                )
                chatDao.updateLastMessage(
                    channelId = message.roomId,
                    message = message.text,
                    type = MessageType.TEXT,
                    time = message.createTime,
                )
                chatDao.setStatus(message.roomId, ChatStatus.NEW)
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)

        Log.d(TAG, "onClosed")
        Log.d(TAG, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)

        Log.d(TAG, "onFailure")
        Log.d(TAG, t.message.toString())
        Log.d(TAG, response?.message ?: "")
    }

    companion object {
        const val TAG = "WEBSOCKET"
    }
}