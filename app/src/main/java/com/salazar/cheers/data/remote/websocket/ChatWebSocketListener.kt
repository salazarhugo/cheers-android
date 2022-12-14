package com.salazar.cheers.data.remote.websocket

import android.util.Log
import cheers.chat.v1.Message
import com.google.protobuf.util.JsonFormat
import com.salazar.cheers.data.ProtoJsonUtil
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.data.mapper.toTextMessage
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
        val message = ProtoJsonUtil.fromJson(text, Message::class.java)

        message?.let {
            Log.d(TAG, message.roomId)
            Log.d(TAG, message.text)
            runBlocking {
                chatDao.insertMessage(message.toTextMessage())
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