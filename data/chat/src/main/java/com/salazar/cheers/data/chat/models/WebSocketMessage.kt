package com.salazar.cheers.data.chat.models

data class WebSocketMessage(
    val type: WebSocketMessageType,
    val userId: String = "",
    val isViewer: Boolean = false,
    val chatMessage: WebSocketChatMessage? = null,
    val chat: WebSocketChat? = null,
    val typing: Typing? = null,
    val presence: Presence? = null,
)

data class WebSocketChat(
    val id: String,
    val name: String,
    val picture: String?,
    val verified: Boolean?,
    val lastMessageTime: Long,
    val lastMessageText: String,
    val status: WebSocketChatStatus,
)

data class WebSocketChatMessage(
    val id: String,
    val chatId: String,
    val userId: String,
    val text: String,
    val createdAt: Long,
)

data class Typing(
    val chatId: String,
    val isTyping: Boolean,
)

data class Presence(
    val chatId: String,
    val isPresent: Boolean,
)

enum class WebSocketChatStatus {
    EMPTY,
    OPENED,
    SENT,
    RECEIVED,
    NEW,
}

enum class WebSocketMessageType {
    CHAT,
    MESSAGE,
    TYPING,
    PRESENCE,
}

fun WebSocketChatStatus.toChatStatus(): ChatStatus {
    return when(this) {
        WebSocketChatStatus.EMPTY -> ChatStatus.EMPTY
        WebSocketChatStatus.OPENED -> ChatStatus.OPENED
        WebSocketChatStatus.SENT -> ChatStatus.SENT
        WebSocketChatStatus.RECEIVED -> ChatStatus.RECEIVED
        WebSocketChatStatus.NEW -> ChatStatus.NEW
    }
}

fun WebSocketChat.toChat(): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        status = status.toChatStatus(),
        type = ChatType.DIRECT,
        picture = picture,
        verified = verified ?: false,
    )
}

fun WebSocketChatMessage.toChatMessage(isSender: Boolean): ChatMessage {
    return ChatMessage(
        id = id,
        roomId = chatId,
        text = text,
        senderId = userId,
        createTime = createdAt,
        type = MessageType.TEXT,
        hasLiked = false,
        isSender = isSender,
        likedBy = emptyList(),
        photoUrl = "",
        seenBy = emptyList(),
        senderName = "",
        senderProfilePictureUrl = "",
        senderUsername = "",
        status = ChatMessageStatus.DELIVERED,
    )
}
