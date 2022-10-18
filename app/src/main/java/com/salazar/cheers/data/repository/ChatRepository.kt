package com.salazar.cheers.data.repository

//import cheers.type.PostOuterClass
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import cheers.chat.v1.*
import com.google.firebase.auth.FirebaseAuth
import com.google.protobuf.Timestamp
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.data.mapper.toChatChannel
import com.salazar.cheers.data.mapper.toTextMessage
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.internal.User
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

    suspend fun getMessages(channelId: String): Flow<List<ChatMessage>> =
        withContext(Dispatchers.IO) {
            return@withContext chatDao.getMessages(channelId = channelId)
        }

    suspend fun joinChannel(channelId: String) = withContext(Dispatchers.IO) {
        try {
            launch {
                chatDao.seenChannel(channelId)
            }
            chatService.joinRoom(JoinRoomRequest.newBuilder().setRoomId(channelId).build())
                ?.collect {
                    chatDao.insertMessage(it.toTextMessage().copy(acknowledged = true))
                }
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    suspend fun getRoomMembers(roomId: String): List<UserCard> {
        try {
            val users = chatService.getRoomMembers(RoomId.newBuilder().setRoomId(roomId).build())
            return users.usersList
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    fun getChannel(channelId: String): Flow<ChatChannel> {
        return chatDao.getChannelFlow(channelId = channelId)
    }

    suspend fun getChatWithUser(userId: String): ChatChannel  = withContext(Dispatchers.IO) {
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
        user: User = User(),
    ): String = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val request = CreateChatReq.newBuilder()
            .setGroupName(groupName)
            .addAllUserIds(UUIDs)
            .build()

        val chatChannel = try {
            chatService.createChat(request).toChatChannel(uid)
        } catch (e: StatusException) {
            e.printStackTrace()
            ChatChannel(
                id = groupName,
                name = user.username,
                accountId = uid,
                picture = user.picture,
                verified = user.verified,
                type = RoomType.DIRECT,
                members = UUIDs,
            )
        }

        chatDao.insert(chatChannel)

        return@withContext chatChannel.id
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

    suspend fun deleteChats(channelId: String) = chatDao.deleteChannel(channelId)

    suspend fun deleteRoom(channelId: String) = withContext(Dispatchers.IO) {
        val request = RoomId.newBuilder()
            .setRoomId(channelId)
            .build()

        chatDao.deleteChannel(channelId)
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
    ): MessageAck? = withContext(Dispatchers.IO) {
        val user = userRepository.getCurrentUser()

        val msg = Message.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setCreated(Timestamp.newBuilder().setSeconds(Date().time / 1000).build())
            .setSender(FirebaseAuth.getInstance().currentUser?.uid)
            .setSenderName(user.name)
            .setSenderUsername(user.username)
            .setSenderpicture(user.picture)
            .setRoom(Room.newBuilder().setId(channelId).build())
            .setPhotoUrl(photoUrl)
            .setType(MessageType.IMAGE)
            .build()

        launch {
            chatDao.insertMessage(msg.toTextMessage())
        }

        val message = flow<Message> {
            emit(msg)
        }

        val acknowledge = chatService.sendMessage(message)

        if (acknowledge?.status == "SENT")
            chatDao.insertMessage(msg.toTextMessage().copy(acknowledged = true))

        return@withContext acknowledge
    }

    suspend fun sendMessage(
        channelId: String,
        text: String
    ): Resource<MessageAck> {
        return withContext(Dispatchers.IO) {
            val user = userRepository.getCurrentUser()

            val msg = Message.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setCreated(Timestamp.newBuilder().setSeconds(Date().time / 1000).build())
                .setSender(FirebaseAuth.getInstance().currentUser?.uid)
                .setSenderName(user.name)
                .setSenderUsername(user.username)
                .setSenderpicture(user.picture)
                .setRoom(Room.newBuilder().setId(channelId).build())
                .setMessage(text)
                .setType(MessageType.TEXT)
                .build()

            launch {
                val message = msg.toTextMessage()
                chatDao.insertMessage(message)
                chatDao.updateLastMessage(channelId, message.text, message.time, message.type)
            }

            val message = flow<Message> {
                emit(msg)
            }

            try {
                val acknowledge = chatService.sendMessage(message)

                if (acknowledge.status == "SENT")
                    chatDao.insertMessage(msg.toTextMessage().copy(acknowledged = true))

                return@withContext Resource.Success(acknowledge)
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

    suspend fun listenRooms() = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        try {
            chatService.getRooms(request = Empty.getDefaultInstance()).collect {
                chatDao.insert(it.toChatChannel(uid))
            }
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    fun getChannels(): Flow<List<ChatChannel>> {
        return chatDao.getChannels()
    }
}