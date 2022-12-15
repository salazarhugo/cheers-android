package com.salazar.cheers.data.repository

//import cheers.type.PostOuterClass
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import cheers.chat.v1.*
import cheers.chat.v1.Message
import cheers.chat.v1.MessageType
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.mapper.toChatChannel
import com.salazar.cheers.data.mapper.toTextMessage
import com.salazar.cheers.data.mapper.toUserItem
import com.salazar.cheers.internal.*
import com.salazar.cheers.internal.RoomStatus
import com.salazar.cheers.internal.RoomType
import com.salazar.cheers.workers.UploadImageMessage
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    application: Application,
    private val chatDao: ChatDao,
    private val userRepository: UserRepository,
    private val chatService: ChatServiceGrpcKt.ChatServiceCoroutineStub,
) {

    private val workManager = WorkManager.getInstance(application)
    private var uid: String? = null

    fun getUnreadChatCount(): Flow<Int> {
        return try {
            chatDao.getUnreadChatCount()
        } catch (e: Exception) {
            flow { emit(0) }
        }
    }

    suspend fun getMessagesFromRemote(channelId: String) {
        try {
            val request = ListRoomMessagesRequest.newBuilder()
                .setRoomId(channelId)
                .build()

            val response = chatService.listRoomMessages(request)
            val messages = response.messagesList.map { it.message.toTextMessage() }
            chatDao.insertMessages(messages)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getMessages(channelId: String): Flow<List<ChatMessage>> =
        withContext(Dispatchers.IO) {
            return@withContext chatDao.getMessages(channelId = channelId)
        }

    suspend fun joinChannel(channelId: String) = withContext(Dispatchers.IO) {
        try {
            launch {
                chatDao.seenChannel(channelId)
            }
            val request = JoinRoomRequest.newBuilder()
                .setRoomId(channelId)
                .build()

            chatService.joinRoom(request).collect {
                chatDao.insertMessage(it.toTextMessage())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getRoomMembers(roomId: String): Result<List<UserItem>> {
        try {
            val request = ListMembersRequest.newBuilder()
                .setRoomId(roomId)
                .build()

            val users = chatService.listMembers(request)
            return Result.Success(users.usersList.map { it.toUserItem() })
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error("Failed to get room memebers")
        }
    }

    fun getChannel(channelId: String): Flow<ChatChannel> {
        return chatDao.getChannelFlow(channelId = channelId)
    }

    suspend fun getChatWithUser(userId: String): ChatChannel = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val user = userRepository.getUserFlow(userId).first()
        return@withContext chatDao.getChatWithUser(userId = userId) ?: ChatChannel(
            id = "temp",
            name = user.username,
            accountId = uid,
            picture = user.picture,
            verified = user.verified,
            type = RoomType.DIRECT,
        )
    }

    suspend fun createGroupChat(
        groupName: String,
        UUIDs: List<String>,
    ): Result<String> = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val request = CreateRoomRequest.newBuilder()
            .setGroupName(groupName)
            .addAllRecipientUsers(UUIDs)
            .build()

        try {
            val chatChannel = chatService.createRoom(request).room.toChatChannel(uid)
            chatDao.insert(chatChannel)
            return@withContext Result.Success(chatChannel.id)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.Error("Failed to create group chat")
        }
    }

    suspend fun leaveRoom(roomId: String) = withContext(Dispatchers.IO) {
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

    suspend fun deleteChats(channelId: String) {
        chatDao.deleteChannel(channelId)
        chatDao.deleteChannelMessages(channelId)
    }

    suspend fun deleteRoom(channelId: String) = withContext(Dispatchers.IO) {
        val request = DeleteRoomRequest.newBuilder()
            .setRoomId(channelId)
            .build()

        chatDao.deleteChannel(channelId)
        chatDao.deleteChannelMessages(channelId)
        chatService.deleteRoom(request)
    }

    suspend fun getRoomId(request: GetRoomIdReq) = withContext(Dispatchers.IO) {
        return@withContext chatService.getRoomId(request = request)
    }

    suspend fun startTyping(channelId: String) = withContext(Dispatchers.IO) {
        try {
            val user = userRepository.getCurrentUser()
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

    suspend fun sendImage(
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

    suspend fun sendImageMessage(
        channelId: String,
        photoUrl: String
    ): SendMessageResponse = withContext(Dispatchers.IO) {
        val user = userRepository.getCurrentUser()

        val msg = Message.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSenderId(FirebaseAuth.getInstance().currentUser?.uid)
            .setSenderName(user.name)
            .setSenderUsername(user.username)
            .setSenderPicture(user.picture)
            .setRoomId(channelId)
            .setPicture(photoUrl)
            .setType(MessageType.IMAGE)
            .build()

        launch {
            chatDao.insertMessage(msg.toTextMessage())
        }

        val message = flow<Message> {
            emit(msg)
        }

        val acknowledge = chatService.sendMessage(message)
        chatDao.insertMessage(msg.toTextMessage())

        return@withContext acknowledge
    }

    suspend fun sendMessage(
        channelId: String,
        text: String
    ): Resource<SendMessageResponse> {
        return withContext(Dispatchers.IO) {
            val user = userRepository.getCurrentUser()

            val msg = Message.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSenderId(FirebaseAuth.getInstance().currentUser?.uid)
                .setSenderName(user.name)
                .setSenderUsername(user.username)
                .setSenderPicture(user.picture)
                .setRoomId(channelId)
                .setText(text)
                .setType(MessageType.TEXT)
                .setStatus(Message.Status.EMPTY)
                .build()

            launch {
                val message = msg.toTextMessage()
                chatDao.insertMessage(message)
                chatDao.updateLastMessage(channelId, message.text, message.createTime, message.type)
            }

            val message = flow<Message> {
                emit(msg)
            }

            try {
                val response = chatService.sendMessage(message)
                chatDao.insertMessage(msg.toTextMessage().copy(status = ChatMessageStatus.READ))

                return@withContext Resource.Success(response)
            } catch (e: StatusException) {
                e.printStackTrace()
                return@withContext Resource.Error(e.localizedMessage)
            }
        }
    }

    suspend fun addToken(token: String) = withContext(Dispatchers.IO) {
        try {
            val request = AddTokenReq.newBuilder()
                .setToken(token)
                .build()

            chatService.addToken(request = request)
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    suspend fun getInbox() = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        try {
            val request = GetInboxRequest.newBuilder()
                .build()
            val response = chatService.getInbox(request = request)
            response.inboxList.forEach { roomWithMessages ->
                val chatChannel = roomWithMessages.room.toChatChannel(uid)
                val messages = roomWithMessages.messagesList.map { it.toTextMessage() }
                chatDao.insert(chatChannel)
                chatDao.insertMessages(messages)
            }
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    fun getChannels(): Flow<List<ChatChannel>> {
        return chatDao.getChannels()
    }
}