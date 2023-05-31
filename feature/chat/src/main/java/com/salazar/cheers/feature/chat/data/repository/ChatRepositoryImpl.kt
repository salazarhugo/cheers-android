package com.salazar.cheers.feature.chat.data.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import cheers.chat.v1.AddTokenReq
import cheers.chat.v1.ChatServiceGrpcKt
import cheers.chat.v1.CreateRoomRequest
import cheers.chat.v1.DeleteRoomRequest
import cheers.chat.v1.GetInboxRequest
import cheers.chat.v1.GetRoomIdReq
import cheers.chat.v1.RoomId
import cheers.chat.v1.SendMessageRequest
import cheers.chat.v1.SendMessageResponse
import cheers.chat.v1.TypingReq
import cheers.type.UserOuterClass
import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.feature.chat.data.db.ChatDao
import com.salazar.cheers.feature.chat.data.mapper.toChatChannel
import com.salazar.cheers.feature.chat.data.mapper.toTextMessage
import com.salazar.cheers.feature.chat.data.worker.UploadImageMessage
import com.salazar.cheers.feature.chat.domain.models.RoomStatus
import com.salazar.cheers.feature.chat.domain.models.RoomType
import com.salazar.cheers.feature.chat.domain.models.ChatChannel
import com.salazar.cheers.feature.chat.domain.models.ChatMessage
import com.salazar.cheers.feature.chat.domain.models.ChatMessageStatus
import com.salazar.common.util.Resource
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    application: Application,
    private val chatDao: ChatDao,
    private val chatService: ChatServiceGrpcKt.ChatServiceCoroutineStub,
) : ChatRepository {

    private val workManager = WorkManager.getInstance(application)

    override fun getUnreadChatCount(): Flow<Int> {
        return try {
            chatDao.getUnreadChatCount()
        } catch (e: Exception) {
            flow { emit(0) }
        }
    }

    override fun pinRoom(roomId: String) {
        chatDao.updatePinnedRoom(roomId = roomId, pinned = true)
    }

    override suspend fun getMessages(channelId: String): Flow<List<ChatMessage>> =
        withContext(Dispatchers.IO) {
            return@withContext chatDao.getMessages(channelId = channelId)
        }

    override fun getChannel(channelId: String): Flow<ChatChannel> {
        return chatDao.getChannelFlow(channelId = channelId).filterNotNull()
    }

    override suspend fun getChatWithUser(userId: String): ChatChannel =
        withContext(Dispatchers.IO) {
            val user = UserOuterClass.UserItem.newBuilder()
            return@withContext chatDao.getChatWithUser(userId = userId) ?: ChatChannel(
                id = "temp",
                name = user.username,
                picture = user.picture,
                verified = user.verified,
                type = RoomType.DIRECT,
            )
        }

    override suspend fun createGroupChat(
        groupName: String,
        UUIDs: List<String>,
    ): Resource<String> = withContext(Dispatchers.IO) {
        val request = CreateRoomRequest.newBuilder()
            .setGroupName(groupName)
            .addAllRecipientUsers(UUIDs)
            .build()

        try {
            val chatChannel = chatService.createRoom(request).room.toChatChannel()
            chatDao.insert(chatChannel)
            return@withContext Resource.Success(chatChannel.id)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Resource.Error("Failed to create group chat")
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

    override suspend fun setRoomStatus(roomId: String, status: RoomStatus) {
        chatDao.setStatus(roomId, status)
    }

    override suspend fun sendImage(
        channelId: String,
        images: List<Uri>
    ): LiveData<WorkInfo> {
        chatDao.setStatus(channelId, RoomStatus.SENT)
        val uploadWork =
            OneTimeWorkRequestBuilder<UploadImageMessage>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "CHANNEL_ID" to channelId,
                        "IMAGES_URI" to images.map { it.toString() }.toTypedArray(),
                    )
                )
                .build()

        workManager.enqueue(uploadWork)

        return workManager.getWorkInfoByIdLiveData(uploadWork.id)
    }

    override suspend fun sendImageMessage(
        channelId: String,
        photoUrl: String
    ): SendMessageResponse {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(
        roomId: String,
        message: ChatMessage
    ): Resource<SendMessageResponse> {
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(roomId, message.text, message.createTime, message.type)
        chatDao.setStatus(roomId, RoomStatus.SENT)

        val msg = SendMessageRequest.newBuilder()
            .setClientId(message.id)
            .setRoomId(roomId)
            .setText(message.text)
            .build()

        return try {
            val response = chatService.sendMessage(msg)
            val textMessage = response.message.toTextMessage().copy(id = message.id)
            chatDao.insertMessage(textMessage)

            Resource.Success(response)
        } catch (e: StatusException) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage)
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
            chatDao.insertInbox(rooms)

            response.inboxList.forEach { roomWithMessages ->
                val messages = roomWithMessages.messagesList.map {
                    it.toTextMessage().copy(status = ChatMessageStatus.DELIVERED)
                }
                chatDao.insertMessages(messages)
            }
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    override fun getChannels(): Flow<List<ChatChannel>> {
        return chatDao.getChannels()
    }
}