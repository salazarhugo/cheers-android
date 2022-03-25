package com.salazar.cheers.data.repository

import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.ChatDao
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.DirectChannel
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.util.FirestoreChat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val service: Neo4jService,
    private val database: CheersDatabase,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
) {

    suspend fun getChannels(): List<DirectChannel> = withContext(Dispatchers.IO) {
        refreshChannels()
        val a = chatDao.getChannels().map {
            val members = userDao.getUsersWithListOfIds(it.channel.members)
            it.copy(members = members)
        }
        return@withContext a
    }

    suspend fun getChannel(channelId: String): DirectChannel? = withContext(Dispatchers.IO) {
        try {
            val channel = chatDao.getChannel(channelId = channelId)
            return@withContext channel.copy(members = userDao.getUsersWithListOfIds(channel.channel.members))
        } catch (e: Exception) {
            return@withContext null
        }
    }

    private suspend fun refreshChannels() {
        FirestoreChat.getChatChannels {
            GlobalScope.launch {
                chatDao.insertAll(it)
            }
        }
    }
}