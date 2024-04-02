package com.salazar.cheers.data.chat.websocket

import android.util.Log
import com.google.gson.Gson
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.isNotConnected
import com.salazar.cheers.data.chat.db.ChatDao
import com.salazar.cheers.data.chat.models.MessageType
import com.salazar.cheers.data.chat.models.Presence
import com.salazar.cheers.data.chat.models.ChatStatus
import com.salazar.cheers.data.chat.models.Typing
import com.salazar.cheers.data.chat.models.WebSocketChat
import com.salazar.cheers.data.chat.models.WebSocketChatMessage
import com.salazar.cheers.data.chat.models.WebSocketChatStatusUpdate
import com.salazar.cheers.data.chat.models.WebSocketMessage
import com.salazar.cheers.data.chat.models.WebSocketMessageType
import com.salazar.cheers.data.chat.models.toChat
import com.salazar.cheers.data.chat.models.toChatMessage
import com.salazar.cheers.data.chat.models.toChatStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketManager @Inject constructor(
    private val chatDao: ChatDao,
    private val accountRepository: AccountRepository,
) : WebSocketListener() {

    lateinit var websocket: WebSocket

    val websocketState = MutableStateFlow<WebsocketState>(WebsocketState.Loading)
    var retryCount = 0

    private suspend fun getIdToken(): Result<String>  {
        if (accountRepository.isNotConnected()) {
            return Result.failure(Exception("Not connected"))
        }

        val idToken = accountRepository.getAccountFlow().map {
            it?.idToken
        }.firstOrNull() ?: return Result.failure(Exception("failed to get idtoken"))

        return Result.success(idToken)
    }

    suspend fun connect() {
        getIdToken().onSuccess { idToken ->
            val request = Request.Builder()
                .url(Constants.WEBSOCKET_URL)
                .addHeader("Authorization", idToken)
                .build()

            val client = OkHttpClient()

            websocket = client.newWebSocket(request, this)
        }
    }

    suspend fun sendChatEvent(
        chatID: String,
        event: ChatEvent,
    ) {
        when (event) {
            ChatEvent.StartPresence -> {
                sendPresence(
                    chatID = chatID,
                    present = true,
                )
            }

            ChatEvent.EndPresence -> {
                sendPresence(
                    chatID = chatID,
                    present = false,
                )
            }

            ChatEvent.StartTyping -> {
                sendTyping(
                    chatID = chatID,
                    isTyping = true,
                )
            }

            ChatEvent.EndTyping -> {
                sendTyping(
                    chatID = chatID,
                    isTyping = false,
                )
            }
        }
    }

    private fun sendTyping(
        chatID: String,
        isTyping: Boolean,
    ) {
        val msg = WebSocketMessage(
            type = WebSocketMessageType.TYPING,
            typing = Typing(
                chatId = chatID,
                isTyping = isTyping,
            )
        )
        sendWebSocketMessage(
            webSocketMessage = msg,
        )
    }

    private fun sendPresence(
        chatID: String,
        present: Boolean,
    ) {
        val msg = WebSocketMessage(
            type = WebSocketMessageType.PRESENCE,
            presence = Presence(
                chatId = chatID,
                isPresent = present,
            )
        )
        sendWebSocketMessage(
            webSocketMessage = msg,
        )
    }

    private fun sendWebSocketMessage(
        webSocketMessage: WebSocketMessage,
    ) {
        val message = Gson().toJson(webSocketMessage, WebSocketMessage::class.java)
        if (!::websocket.isInitialized)
            return
        val result = websocket.send(text = message)
        Log.d("WEBSOCKET", result.toString())
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        GlobalScope.launch {
            websocketState.emit(WebsocketState.Connected)
        }
        Log.d(ChatWebSocketListener.TAG, "CONNECTED")
        retryCount = 0
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)

        GlobalScope.launch {
            websocketState.emit(WebsocketState.Error)
        }
        Log.d(ChatWebSocketListener.TAG, "onClosed")
        Log.d(ChatWebSocketListener.TAG, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)

        GlobalScope.launch {
            websocketState.emit(WebsocketState.Error)
            if (retryCount >= MAX_RETRY) {
                return@launch
            }

            delay(2000)
            retryCount++
            connect()
        }
        Log.d(ChatWebSocketListener.TAG, "onFailure")
        Log.d(ChatWebSocketListener.TAG, t.message.toString())
        Log.d(ChatWebSocketListener.TAG, response?.message ?: "")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d(ChatWebSocketListener.TAG, text)

        try {
            val message = Gson().fromJson(text, WebSocketMessage::class.java)

            if (message == null) {
                Log.d(
                    ChatWebSocketListener.TAG,
                    "onMessage: failed to parse websocket message from server",
                )
                return
            }

            // Only handle other people chat events
            // Ignore own chat events
            if (message.isViewer) {
                return
            }

            when (message.type) {
                WebSocketMessageType.MESSAGE -> {
                    handleMessage(
                        chatMessage = message.chatMessage,
                        isSender = message.isViewer,
                    )
                }

                WebSocketMessageType.TYPING -> {
                    handleTyping(message.typing)
                }

                WebSocketMessageType.PRESENCE -> {
                    handlePresence(message.presence)
                }

                WebSocketMessageType.CHAT -> {
                    handleChat(message.chat)
                }

                WebSocketMessageType.CHAT_STATUS -> {
                    handleChatStatusUpdate(message.chatStatus)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handlePresence(
        presence: Presence?,
    ) {
        if (presence == null)
            return

        GlobalScope.launch {
            val chat = chatDao.getChannel(presence.chatId)
            chatDao.insert(
                chat.copy(isOtherUserPresent = presence.isPresent)
            )
        }
    }

    private fun handleTyping(
        typing: Typing?,
    ) {
        if (typing == null)
            return

        GlobalScope.launch {
            val chat = chatDao.getChannel(typing.chatId)
            chatDao.insert(
                chat.copy(isOtherUserTyping = typing.isTyping)
            )
        }
    }

    private fun handleChatStatusUpdate(
        chatStatusUpdate: WebSocketChatStatusUpdate?,
    ) {
        if (chatStatusUpdate == null)
            return

        GlobalScope.launch {
            val newChat = chatDao.getChannel(chatStatusUpdate.chatId)
            chatDao.insert(newChat.copy(status = chatStatusUpdate.status.toChatStatus()))
        }
    }

    private fun handleChat(
        chat: WebSocketChat?,
    ) {
        if (chat == null)
            return

        GlobalScope.launch {
            chatDao.insert(chat.toChat())
        }
    }

    private fun handleMessage(
        chatMessage: WebSocketChatMessage?,
        isSender: Boolean,
    ) {
        if (chatMessage == null)
            return

        GlobalScope.launch {
            chatDao.insertMessage(
                chatMessage.toChatMessage(isSender)
            )
            chatDao.updateLastMessage(
                channelId = chatMessage.chatId,
                message = chatMessage.text,
                type = MessageType.TEXT,
                time = chatMessage.createdAt,
            )
            chatDao.setStatus(
                chatMessage.chatId,
                ChatStatus.NEW,
            )
            chatDao.incrementUnreadCount(chatMessage.chatId)
        }
    }

    companion object {
        const val MAX_RETRY = 5
    }
}

sealed class ChatEvent {
    data object StartTyping : ChatEvent()
    data object EndTyping : ChatEvent()
    data object StartPresence : ChatEvent()
    data object EndPresence : ChatEvent()
}

sealed class WebsocketState {
    data object Loading : WebsocketState()
    data object Connected : WebsocketState()
    data object Error : WebsocketState()
}