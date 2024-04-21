package com.salazar.cheers.data.chat.repository

import android.util.Log
import cheers.chat.v1.AddTokenReq
import cheers.chat.v1.ChatServiceGrpcKt
import cheers.chat.v1.CreateRoomRequest
import cheers.chat.v1.DeleteRoomRequest
import cheers.chat.v1.GetInboxRequest
import cheers.chat.v1.GetRoomIdReq
import cheers.chat.v1.ListMembersRequest
import cheers.chat.v1.RoomId
import cheers.chat.v1.SendMessageRequest
import cheers.chat.v1.SendMessageResponse
import cheers.chat.v1.SendReadReceiptRequest
import cheers.chat.v1.TypingReq
import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.core.db.dao.ChatDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.data.chat.mapper.toChatChannel
import com.salazar.cheers.data.chat.mapper.toTextMessage
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.shared.data.mapper.toUserItem
import com.salazar.cheers.shared.data.toDataError
import com.salazar.common.util.result.DataError
import com.salazar.common.util.result.Result
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val chatService: ChatServiceGrpcKt.ChatServiceCoroutineStub,
) : ChatRepository {

    override fun getUnreadChatCount(): Flow<Int> {
        return try {
            chatDao.getUnreadChatCount()
        } catch (e: Exception) {
            flowOf(0)
        }
    }

    override suspend fun sendReadReceipt(chatID: String): Result<Unit, DataError.Network> {
        val request = SendReadReceiptRequest.newBuilder()
            .setRoomId(chatID)
            .build()

        return try {
            chatService.sendReadReceipt(request)
            Result.Success(Unit)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(e.toDataError())
        }
    }

    override fun pinRoom(roomId: String) {
        chatDao.updatePinnedRoom(roomId = roomId, pinned = true)
    }

    override suspend fun listMessages(channelId: String): Flow<List<ChatMessage>> =
        withContext(Dispatchers.IO) {
            return@withContext chatDao.getMessages(channelId = channelId)
                .map { it.asExternalModel() }
        }

    override suspend fun getRoomMembers(roomId: String): Result<List<com.salazar.cheers.core.model.UserItem>, DataError.Network> {
        val request = ListMembersRequest.newBuilder()
            .setRoomId(roomId)
            .setPage(1)
            .setPageSize(10)
            .build()

        return try {
            val users = chatService.listMembers(request).usersList
                .map {
                    it.toUserItem()
                }
            Result.Success(users)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(e.toDataError())
        }
    }

    override fun getChannel(channelId: String): Flow<ChatChannel> {
        return chatDao.getChannelFlow(channelId = channelId).filterNotNull()
            .map { it.asExternalModel() }
    }

    override suspend fun getChatWithUser(userId: String): ChatChannel? =
        withContext(Dispatchers.IO) {
            return@withContext chatDao.getChatWithUser(userId = userId)?.asExternalModel()
        }

    override suspend fun getOrCreateDirectChat(
        otherUserID: String,
    ): Result<ChatChannel, DataError.Network> {
        return getOrCreateGroupChat("", listOf(otherUserID))
    }

    override suspend fun getOrCreateGroupChat(
        groupName: String,
        UUIDs: List<String>,
    ): Result<ChatChannel, DataError.Network> = withContext(Dispatchers.IO) {
        val request = CreateRoomRequest.newBuilder()
            .setGroupName(groupName)
            .addAllRecipientUsers(UUIDs)
            .build()

        return@withContext try {
            val chatChannel = chatService.createRoom(request).room.toChatChannel()
            chatDao.insert(chatChannel.asEntity())
            Result.Success(chatChannel)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(e.toDataError())
        }
    }

    override suspend fun leaveRoom(roomId: String) {
        val request = RoomId.newBuilder()
            .setRoomId(roomId)
            .build()

        try {
            chatDao.deleteChannel(roomId)
            chatService.leaveRoom(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteChats(channelId: String) {
        chatDao.deleteChannel(channelId)
        chatDao.deleteChannelMessages(channelId)
    }

    override suspend fun deleteRoom(channelId: String) = withContext(Dispatchers.IO) {
        try {
            val request = DeleteRoomRequest.newBuilder()
                .setRoomId(channelId)
                .build()

            chatService.deleteRoom(request)
            chatDao.deleteChannel(channelId)
            chatDao.deleteChannelMessages(channelId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun clear() {
        chatDao.clearRooms()
    }

    override suspend fun getRoomId(request: GetRoomIdReq): RoomId {
        return chatService.getRoomId(request = request)
    }

    override suspend fun startTyping(channelId: String) {
        try {
            val user = UserItem.newBuilder()
            chatService.typingStart(
                TypingReq.newBuilder()
                    .setRoomId(channelId)
                    .setUsername(user.name)
                    .setAvatarUrl(user.picture)
                    .build()
            )
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    override suspend fun setRoomStatus(roomId: String, status: ChatStatus) {
        chatDao.setStatus(roomId, status)
    }

//    override suspend fun sendImage(
//        channelId: String,
//        images: List<Uri>
//    ): LiveData<WorkInfo> {
//        chatDao.setStatus(channelId, RoomStatus.SENT)
//        val uploadWork =
//            OneTimeWorkRequestBuilder<UploadImageMessage>()
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .setInputData(
//                    workDataOf(
//                        "CHANNEL_ID" to channelId,
//                        "IMAGES_URI" to images.map { it.toString() }.toTypedArray(),
//                    )
//                )
//                .build()
//
//        workManager.enqueue(uploadWork)
//
//        return workManager.getWorkInfoByIdLiveData(uploadWork.id)
//    }

    override suspend fun sendImageMessage(
        channelId: String,
        photoUrl: String
    ): SendMessageResponse {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(
        roomId: String,
        message: ChatMessage
    ): Result<ChatMessage, DataError.Network> {
        chatDao.insertMessage(message.asEntity())
        chatDao.updateLastMessage(
            roomId,
            message.text,
            message.createTime,
            message.type,
        )
        chatDao.setStatus(roomId, ChatStatus.SENT)

        val msg = SendMessageRequest.newBuilder()
            .setClientId(message.id)
            .setRoomId(roomId)
            .setText(message.text)
            .build()

        return try {
            val response = chatService.sendMessage(msg)
            val textMessage = response.message.toTextMessage().copy(
                id = message.id,
                isSender = true,
            )
            chatDao.insertMessage(textMessage.asEntity())

            Result.Success(textMessage)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(DataError.Network.NO_INTERNET)
        }
    }


    override suspend fun addToken(token: String) {
        try {
            val request = AddTokenReq.newBuilder()
                .setToken(token)
                .build()

            chatService.addToken(request = request)
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    override suspend fun getInbox() {
        try {
            val request = GetInboxRequest.newBuilder()
                .build()
            val response = chatService.getInbox(request = request)

            val rooms = response.inboxList.map { it.room.toChatChannel() }
            chatDao.insertInbox(rooms.asEntity())

            response.inboxList.forEach { roomWithMessages ->
                val messages = roomWithMessages.messagesList.map {
                    it.toTextMessage()
                }
                chatDao.deleteChannelMessages(roomWithMessages.room.id)
                chatDao.insertMessages(messages.asEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("GRPC", e.toString())
        }
    }

    override fun getChannels(): Flow<List<ChatChannel>> {
        return chatDao.getChannels().map { it.asExternalModel() }
    }
}