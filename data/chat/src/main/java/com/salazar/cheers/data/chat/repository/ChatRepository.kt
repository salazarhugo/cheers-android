package com.salazar.cheers.data.chat.repository

import cheers.chat.v1.GetRoomIdReq
import cheers.chat.v1.RoomId
import cheers.chat.v1.SendMessageResponse
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.common.util.result.Result
import com.salazar.common.util.result.DataError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getUnreadChatCount(): Flow<Int>

    suspend fun sendReadReceipt(chatID: String): Result<Unit, DataError.Network>

    fun pinRoom(roomId: String)

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

//    suspend fun sendImage(
//        channelId: String,
//        images: List<Uri>
//    ): LiveData<WorkInfo>

    suspend fun sendImageMessage(
        channelId: String,
        photoUrl: String
    ): SendMessageResponse

    suspend fun sendMessage(
        roomId: String,
        message: ChatMessage,
    ): Result<ChatMessage, DataError.Network>

    suspend fun addToken(token: String)

    suspend fun getInbox()

    fun getChannels(): Flow<List<ChatChannel>>
}