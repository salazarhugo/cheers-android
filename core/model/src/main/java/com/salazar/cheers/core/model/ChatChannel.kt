package com.salazar.cheers.core.model

data class ChatChannel(
    val id: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val members: List<String> = emptyList(),
    val admins: List<String> = emptyList(),
    val admin: Boolean = false,
    val otherUserId: String = "",
    val isOtherUserTyping: Boolean = false,
    val isOtherUserPresent: Boolean = false,
    val createTime: Long = 0,
    val createTimeBy: String = "",
    val picture: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0,
    val lastMessageType: MessageType = MessageType.TEXT,
    val type: ChatType = ChatType.DIRECT,
    val status: ChatStatus = ChatStatus.UNRECOGNIZED,
    val ownerId: String = "",
    val pinned: Boolean = false,
    val unreadCount: Int = 0,
    val membersCount: Int = 0,
)

enum class ChatStatus {
    EMPTY,
    OPENED,
    SENT,
    RECEIVED,
    NEW,
    UNRECOGNIZED,
}

enum class MessageType {
    TEXT, IMAGE, VIDEO
}

enum class ChatType {
    DIRECT,
    GROUP,
    UNRECOGNIZED,
}