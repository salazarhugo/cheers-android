package com.salazar.cheers.data.repository

import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelResponse
import com.salazar.cheers.internal.Message
import com.salazar.cheers.util.FirestoreChat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val service: Neo4jService,
    private val database: CheersDatabase,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
) {

    suspend fun getMessages(channelId: String): Flow<List<Message>> =
        FirestoreChat.getChatMessages(channelId)

    suspend fun getChannels(): Flow<List<ChatChannel>> =
        FirestoreChat.getChatChannelsFlow().map { it.map { it.toChatChannel() } }

    suspend fun getChannel(channelId: String): Flow<ChatChannel> =
        FirestoreChat.getChatChannel(channelId = channelId).map {
            it.toChatChannel()
        }

    private suspend fun ChatChannelResponse.toChatChannel(): ChatChannel {
        return ChatChannel().copy(
            id = id,
            name = name,
            members = userDao.getUsersWithListOfIds(ids = members),
            otherUserId = "",
            createdAt = createdAt,
            createdBy = createdBy,
            recentMessageTime = recentMessageTime,
            recentMessage = recentMessage,
            type = type
        )
    }
}

