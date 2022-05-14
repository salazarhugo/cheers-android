package com.salazar.cheers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.data.paging.StoryRemoteMediator
import com.salazar.cheers.internal.User
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val service: Neo4jService,
    private val database: CheersDatabase
) {

    private val storyDao = database.storyDao()

    suspend fun getMyStories(): Flow<List<Story>> = storyDao.getStoriesByAuthor()

    suspend fun getStories(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = StoryRemoteMediator(database = database, networkService = service),
        ) {
            return@Pager storyDao.pagingSource()
        }.flow
    }

    suspend fun delete(storyId: String) {
        coroutineScope {
            service.deleteStory(storyId = storyId)
        }
        storyDao.deleteWithId(storyId = storyId)
    }

    suspend fun seenStory(storyId: String) {
        coroutineScope {
            service.seenStory(storyId = storyId)
        }
        val story = storyDao.getStory(storyId = storyId)
        storyDao.update(story = story.copy(seenBy = story.seenBy + FirebaseAuth.getInstance().currentUser?.uid!!))
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