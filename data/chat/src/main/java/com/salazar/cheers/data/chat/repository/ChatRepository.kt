package com.salazar.cheers.data.chat.repository

import android.net.Uri
import cheers.chat.v1.GetRoomIdReq
import cheers.chat.v1.RoomId
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatRepository {
    val chatFiltersFlow: MutableStateFlow<ChatFilter>

    fun getUnreadChatCount(): Flow<Int>

    fun updateChatFilter(chatFilter: ChatFilter): Unit

    suspend fun sendReadReceipt(chatID: String): Result<Unit, DataError.Network>

    fun pinRoom(roomId: String)

    suspend fun fetchChatMessages(
        chatChannelID: String,
        page: Int,
        pageSize: Int,
    ): Result<Unit, DataError>

    suspend fun listMessages(channelId: String): Flow<List<ChatMessage>>

    suspend fun getRoomMembers(roomId: String): Result<List<UserItem>, DataError.Network>

    fun getChannel(channelId: String): Flow<ChatChannel>

    suspend fun getChatWithUser(userId: String): ChatChannel?

    suspend fun getOrCreateDirectChat(
        otherUserID: String,
    ): Result<ChatChannel, DataError.Network>

    suspend fun getOrCreateGroupChat(
        groupName: String,
        UUIDs: List<String>,
    ): Result<ChatChannel, DataError.Network>

    suspend fun leaveRoom(roomId: String)
    suspend fun deleteChats(channelId: String)

    suspend fun deleteRoom(channelId: String)
    suspend fun clear()

    suspend fun getRoomId(request: GetRoomIdReq): RoomId

    suspend fun startTyping(channelId: String)

    suspend fun setRoomStatus(
        roomId: String,
        status: ChatStatus,
    )

    suspend fun enqueueChatMessage(
        chatChannelID: String,
        text: String,
        images: List<Uri>,
        replyTo: String?,
    ): Result<Unit, DataError.Network>

    suspend fun sendChatMessageLocal(
        chatMessage: ChatMessage,
    ): Result<Unit, DataError.Local>

    suspend fun sendChatMessage(
        chatChannelID: String,
        chatMessage: ChatMessage,
        replyTo: String?,
    ): Result<ChatMessage, DataError.Network>

    suspend fun deleteChatMessage(
        chatID: String,
        chatMessageID: String,
    ): Result<Unit, DataError.Network>

    suspend fun addToken(token: String)

    suspend fun getInbox()

    fun getChannels(): Flow<List<ChatChannel>>
}