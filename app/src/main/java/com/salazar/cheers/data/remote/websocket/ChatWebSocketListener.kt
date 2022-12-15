package com.salazar.cheers.data.remote.websocket

import android.util.Log
import com.google.gson.Gson
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.internal.ChatMessageStatus
import com.salazar.cheers.internal.MessageType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class ChatWebSocketListener @Inject constructor(
    private val chatDao: ChatDao,
) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        webSocket.send("G")
        Log.d("WEBSOCKET MESSAGE", "WEBSOCKET OPEN")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d(TAG, text)

        val message = Gson().fromJson(text, ChatMessage::class.java)
        Log.d(TAG, message.toString())

        message?.let {
            runBlocking {
                chatDao.insertMessage(
                    message.copy(
                        likedBy = emptyList(),
                        seenBy = emptyList(),
                        photoUrl = "",
                        senderUsername = "",
                        senderProfilePictureUrl = "",
                        type = MessageType.TEXT,
                        status = ChatMessageStatus.EMPTY,
                    )
                )
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)

        Log.d(TAG, "onClosed")
        Log.d(TAG, reason)
    }

    companion object {
        const val TAG = "WEBSOCKET"
    }
}