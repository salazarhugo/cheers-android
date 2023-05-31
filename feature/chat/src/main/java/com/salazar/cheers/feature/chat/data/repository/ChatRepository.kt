package com.salazar.cheers.feature.chat.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import cheers.chat.v1.GetRoomIdReq
import cheers.chat.v1.RoomId
import cheers.chat.v1.SendMessageResponse
import com.salazar.cheers.feature.chat.domain.models.ChatChannel
import com.salazar.cheers.feature.chat.domain.models.ChatMessage
import com.salazar.cheers.feature.chat.domain.models.RoomStatus
import com.salazar.common.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getUnreadChatCount(): Flow<Int>

    fun pinRoom(roomId: String)

    suspend fun getMessages(channelId: String): Flow<List<ChatMessage>>

//    suspend fun getRoomMembers(roomId: String): Result<List<UserItem>>

    fun getChannel(channelId: String): Flow<ChatChannel>

    suspend fun getChatWithUser(userId: String): ChatChannel

    suspend fun createGroupChat(
        groupName: String,
        UUIDs: List<String>,
    ): Resource<String>

    suspend fun leaveRoom(roomId: String)
    suspend fun deleteChats(channelId: String)

    suspend fun deleteRoom(channelId: String)

    suspend fun getRoomId(request: GetRoomIdReq): RoomId

    suspend fun startTyping(channelId: String)

    suspend fun setRoomStatus(roomId: String, status: RoomStatus)

    suspend fun sendImage(
        channelId: String,
        images: List<Uri>
    ): LiveData<WorkInfo>

    suspend fun sendImageMessage(
        channelId: String,
        photoUrl: String
    ): SendMessageResponse

    suspend fun sendMessage(
        roomId: String,
        message: ChatMessage,
    ): Resource<SendMessageResponse>

    suspend fun addToken(token: String)

    suspend fun getInbox()

    fun getChannels(): Flow<List<ChatChannel>>
}