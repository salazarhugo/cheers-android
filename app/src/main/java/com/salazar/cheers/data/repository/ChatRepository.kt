package com.salazar.cheers.data.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.protobuf.Timestamp
import com.salazar.cheers.*
import com.salazar.cheers.backend.ChatService
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.data.mapper.toChatChannel
import com.salazar.cheers.data.mapper.toTextMessage
import com.salazar.cheers.data.remote.ErrorHandleInterceptor
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.workers.UploadImageMessage
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
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
    private val userRepository: UserRepository
) {
    private val workManager = WorkManager.getInstance(application)
    private lateinit var managedChannel: ManagedChannel

    private fun getClient(): ChatService? {
        try {
            val user2 =
                FirebaseAuth.getInstance().currentUser ?: throw Exception("User is not logged in.")
            val task: Task<GetTokenResult> = user2.getIdToken(false)
            val tokenResult = Tasks.await(task)
            val idToken = tokenResult.token ?: throw Exception("idToken is null")

            managedChannel = ManagedChannelBuilder
                .forAddress("chat-r3a2dr4u4a-nw.a.run.app", 443)
                .build()

            val client = ChatServiceGrpcKt
                .ChatServiceCoroutineStub(managedChannel)
                .withInterceptors(ErrorHandleInterceptor(idToken = idToken))

            return ChatService(client)
        } catch (e: Exception) {
            Log.e("Chat Repository", e.toString())
            return null
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
            getClient()!!.joinRoom(RoomId.newBuilder().setRoomId(channelId).build()).collect {
                chatDao.insertMessage(it.toTextMessage().copy(acknowledged = true))
            }
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    fun getChannel(channelId: String): Flow<ChatChannel> {
        return chatDao.getChannelFlow(channelId = channelId)
    }

    suspend fun createGroupChat(
        groupName: String,
        UUIDs: List<String>
    ): String = withContext(Dispatchers.IO) {
        val request = CreateChatReq.newBuilder()
            .setGroupName(groupName)
            .addAllUserIds(UUIDs)
            .build()

        val room = getClient()!!.createChat(request)
        chatDao.insert(room.toChatChannel())
        return@withContext room.id
    }

    suspend fun leaveRoom(channelId: String) = withContext(Dispatchers.IO) {
        val request = RoomId.newBuilder()
            .setRoomId(channelId)
            .build()

        chatDao.deleteChannel(channelId)
        getClient()!!.leaveRoom(request)
    }

    suspend fun deleteChats(channelId: String) = chatDao.deleteChannel(channelId)

    suspend fun deleteRoom(channelId: String) = withContext(Dispatchers.IO) {
        val request = RoomId.newBuilder()
            .setRoomId(channelId)
            .build()

        chatDao.deleteChannel(channelId)
        getClient()!!.deleteRoom(request)
    }

    suspend fun startTyping(channelId: String) = withContext(Dispatchers.IO) {
        try {
            val user = userRepository.getCurrentUser()
            getClient()!!.typingStart(
                TypingReq.newBuilder()
                    .setRoomId(channelId)
                    .setUsername(user.name)
                    .setAvatarUrl(user.profilePictureUrl)
                    .build()
            )
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    suspend fun sendImage(channelId: String, images: List<Uri>): LiveData<WorkInfo> {
        chatDao.setStatus(channelId, RoomStatus.SENDING)
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
    ): MessageAck = withContext(Dispatchers.IO) {
        val user = userRepository.getCurrentUser()

        val msg = Message.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setCreated(Timestamp.newBuilder().setSeconds(Date().time / 1000).build())
            .setSender(FirebaseAuth.getInstance().currentUser?.uid)
            .setSenderName(user.name)
            .setSenderUsername(user.username)
            .setSenderProfilePictureUrl(user.profilePictureUrl)
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

        val acknowledge = getClient()!!.sendMessage(message)

        if (acknowledge.status == "SENT")
            chatDao.insertMessage(msg.toTextMessage().copy(acknowledged = true))

        return@withContext acknowledge
    }

    suspend fun sendMessage(
        channelId: String,
        text: String
    ) = withContext(Dispatchers.IO) {
        val user = userRepository.getCurrentUser()

        val msg = Message.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setCreated(Timestamp.newBuilder().setSeconds(Date().time / 1000).build())
            .setSender(FirebaseAuth.getInstance().currentUser?.uid)
            .setSenderName(user.name)
            .setSenderUsername(user.username)
            .setSenderProfilePictureUrl(user.profilePictureUrl)
            .setRoom(Room.newBuilder().setId(channelId).build())
            .setMessage(text)
            .setType(MessageType.TEXT)
            .build()

        launch {
            chatDao.insertMessage(msg.toTextMessage())
        }

        val message = flow<Message> {
            emit(msg)
        }

        val acknowledge = getClient()!!.sendMessage(message)

        if (acknowledge.status == "SENT")
            chatDao.insertMessage(msg.toTextMessage().copy(acknowledged = true))

        return@withContext acknowledge
    }

    suspend fun addToken(token: String) = withContext(Dispatchers.IO) {
        try {
            val request = AddTokenReq.newBuilder()
                .setToken(token)
                .build()

            getClient()!!.addToken(request = request)
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }

    fun getChannels(): Flow<List<ChatChannel>> {
        return chatDao.getChannels()
    }

    suspend fun fetchRoomsFromRemote() = withContext(Dispatchers.IO) {
        try {
            getClient()!!.getRooms(request = Empty.getDefaultInstance()).collect {
                chatDao.insert(it.toChatChannel())
            }
        } catch (e: Exception) {
            Log.e("GRPC", e.toString())
        }
    }
}