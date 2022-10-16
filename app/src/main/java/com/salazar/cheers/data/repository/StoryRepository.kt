package com.salazar.cheers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val database: CheersDatabase
) {

    private val storyDao = database.storyDao()

    fun getMyStories(): Flow<List<Story>> = storyDao.getStoriesByAuthor()

    fun getStories(): Flow<PagingData<Story>> {
        return emptyFlow()
    }

    suspend fun getUserStory(username: String): Flow<List<Story>> {
        return flow {
            val localStories = storyDao.getUserStory(username = username)
            emit(localStories)

            val remoteStories = try {
//                coreService.getUserStory(username = username)
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun addStory(story: Story) = withContext(Dispatchers.IO) {
        try {
//            coreService.createStory(story = story)
            storyDao.insert(story)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun seenRemote(storyId: String) {
        try {
//            coreService.seenStory(storyId = storyId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRemote(storyId: String) {
        try {
//            coreService.deleteStory(storyId = storyId)
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

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}