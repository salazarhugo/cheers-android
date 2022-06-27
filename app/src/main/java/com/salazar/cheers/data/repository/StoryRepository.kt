package com.salazar.cheers.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.paging.StoryRemoteMediator
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import com.salazar.cheers.internal.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val coreService: CoreService,
    private val service: Neo4jService,
    private val database: CheersDatabase
) {

    private val storyDao = database.storyDao()

    fun getMyStories(): Flow<List<Story>> = storyDao.getStoriesByAuthor()

    fun getStories(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = StoryRemoteMediator(database = database, networkService = coreService),
        ) {
            return@Pager storyDao.pagingSource()
        }.flow
    }

    suspend fun getUserStory(username: String): Flow<List<Story>> {
        return flow {
            val localStories = storyDao.getUserStory(username = username)
            emit(localStories)

            val remoteStories = try {
                coreService.getUserStory(username = username)
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                e.printStackTrace()
                null
            }

            remoteStories?.let { stories ->
                storyDao.insertAll(stories)
                emit(storyDao.getUserStory(username = username))
            }
        }
    }

    suspend fun addStory(story: Story) = withContext(Dispatchers.IO) {
        try {
            coreService.createStory(story = story)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun seenRemote(storyId: String) {
        try {
            coreService.seenStory(storyId = storyId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRemote(storyId: String) {
        try {
            coreService.deleteStory(storyId = storyId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun delete(storyId: String) {
        coroutineScope {
            deleteRemote(storyId = storyId)
        }
        storyDao.deleteWithId(storyId = storyId)
    }

    suspend fun seenStory(storyId: String) {
        coroutineScope {
            seenRemote(storyId = storyId)
        }
        val story = storyDao.getStory(storyId = storyId)
        storyDao.update(story = story.copy(seen = true))
    }

    fun sendReaction(
        user: User,
        text: String
    ) {
//        FirestoreChat.getOrCreateChatChannel(user.id) { channelId ->
//            val textMessage =
//                TextMessage().copy(
//                    senderId = FirebaseAuth.getInstance().currentUser?.uid!!,
//                    text = text,
//                    senderName = user.name,
//                    senderUsername = user.username,
//                    chatChannelId = channelId,
//                    senderProfilePictureUrl = user.profilePictureUrl,
//                    type = MessageType.TEXT,
//                )
//            FirestoreChat.sendMessage(textMessage, channelId = channelId)
//        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}