package com.salazar.cheers.data.chat.repository

import android.net.Uri
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import cheers.chat.v1.AddTokenReq
import cheers.chat.v1.ChatServiceGrpcKt
import cheers.chat.v1.CreateRoomRequest
import cheers.chat.v1.DeleteMessageRequest
import cheers.chat.v1.DeleteRoomRequest
import cheers.chat.v1.GetInboxRequest
import cheers.chat.v1.GetRoomIdReq
import cheers.chat.v1.ListMembersRequest
import cheers.chat.v1.ListRoomMessagesRequest
import cheers.chat.v1.RoomId
import cheers.chat.v1.SendMessageRequest
import cheers.chat.v1.SendReadReceiptRequest
import cheers.chat.v1.TypingReq
import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.core.db.dao.ChatDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.EmptyChatMessage
import com.salazar.cheers.data.chat.SendChatMessageWorker
import com.salazar.cheers.data.chat.mapper.toChatChannel
import com.salazar.cheers.data.chat.mapper.toTextMessage
import com.salazar.cheers.shared.data.mapper.toUserItem
import com.salazar.cheers.shared.data.toDataError
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


enum class ChatFilter {
    NONE,
    GROUPS,
    UNREPLIED,
}

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val chatService: ChatServiceGrpcKt.ChatServiceCoroutineStub,
    private val workManager: WorkManager,
) : ChatRepository {

    override val chatFiltersFlow: MutableStateFlow<ChatFilter> = MutableStateFlow(ChatFilter.NONE)

    override fun getUnreadChatCount(): Flow<Int> {
        return try {
            chatDao.getUnreadChatCount()
        } catch (e: Exception) {
            flowOf(0)
        }
    }

    override fun updateChatFilter(chatFilter: ChatFilter) {
        chatFiltersFlow.update { chatFilter }
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

    override suspend fun fetchChatMessages(
        chatChannelID: String,
        page: Int,
        pageSize: Int,
    ): Result<Unit, DataError> {
        val request = ListRoomMessagesRequest.newBuilder()
            .setRoomId(chatChannelID)
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        return try {
            val response = chatService.listRoomMessages(request)
            val chatMessages = response.messagesList.map { it.toTextMessage() }
            chatDao.insertMessages(chatMessages.asEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.toDataError())
        }
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

    override suspend fun enqueueChatMessage(
        chatChannelID: String,
        text: String,
        images: List<Uri>,
        replyTo: String?
    ): Result<Unit, DataError.Network> {
        val id = UUID.randomUUID().toString()
        val localChatMessage = EmptyChatMessage.copy(
            id = id,
            roomId = chatChannelID,
            text = text,
            status = ChatMessageStatus.SCHEDULED,
            images = images.map { it.toString() },
            createTime = Date().time,
            isSender = true,
            hasLiked = false,
        )
        sendChatMessageLocal(chatMessage = localChatMessage)

        enqueueChatMessage(
            id,
            "CHAT_MESSAGE_ID" to id,
            "CHAT_CHANNEL_ID" to chatChannelID,
            "IMAGES_URI" to images.map { it.toString() }.toTypedArray(),
            "TEXT" to text,
            "REPLY_TO" to replyTo,
        )

        return Result.Success(Unit)
    }

    private fun enqueueChatMessage(
        chatMessageID: String,
        vararg pairs: Pair<String, Any?>,
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest =
            OneTimeWorkRequestBuilder<SendChatMessageWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(*pairs))
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniqueWork(
            chatMessageID,
            ExistingWorkPolicy.REPLACE,
            uploadWorkRequest,
        )
    }

    override suspend fun sendChatMessageLocal(
        chatMessage: ChatMessage,
    ): Result<Unit, DataError.Local> {
        chatDao.insertMessage(
            message = chatMessage.asEntity()
        )
        chatDao.updateLastMessage(
            channelId = chatMessage.roomId,
            message = chatMessage.text,
            time = chatMessage.createTime,
            type = chatMessage.type,
        )
        chatDao.setStatus(
            channelId = chatMessage.roomId,
            status = ChatStatus.SENT,
        )

        return Result.Success(Unit)
    }

    override suspend fun sendChatMessage(
        chatChannelID: String,
        chatMessage: ChatMessage,
        replyTo: String?,
    ): Result<ChatMessage, DataError.Network> {
        var msg = SendMessageRequest.newBuilder()
            .setClientId(chatMessage.id)
            .setRoomId(chatChannelID)
            .setText(chatMessage.text)
            .addAllMediaIds(chatMessage.images)

        if (replyTo != null) {
            msg = msg.setReplyTo(replyTo)
        }

        return try {
            val response = chatService.sendMessage(msg.build())
            val textMessage = response.message.toTextMessage().copy(
                id = chatMessage.id,
                isSender = true,
            )
            chatDao.insertMessage(textMessage.asEntity())

            Result.Success(textMessage)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(DataError.Network.NO_INTERNET)
        }
    }

    override suspend fun deleteChatMessage(
        chatID: String,
        chatMessageID: String,
    ): Result<Unit, DataError.Network> {
        return try {
            val request = DeleteMessageRequest.newBuilder()
                .setRoomId(chatID)
                .setMessageId(chatMessageID)
                .build()

            chatService.deleteMessage(request = request)
            chatDao.deleteChatMessage(chatMessageID = chatMessageID)

            Result.Success(Unit)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(e.toDataError())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Network.UNKNOWN)
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