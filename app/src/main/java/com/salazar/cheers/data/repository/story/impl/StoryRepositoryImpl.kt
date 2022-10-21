package com.salazar.cheers.data.repository.story.impl

import cheers.story.v1.*
import androidx.paging.PagingData
import cheers.type.StoryOuterClass
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.repository.story.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.StoryDao
import com.salazar.cheers.data.mapper.toStory

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val storyDao: StoryDao,
    private val service: StoryServiceGrpcKt.StoryServiceCoroutineStub,
): StoryRepository {

    override suspend fun createStory(story: StoryOuterClass.Story): Result<Unit> {
        val request = CreateStoryRequest.newBuilder()
            .setStory(story)
            .build()

        return try {
            val response = service.createStory(request)
            storyDao.insert(response.toStory())
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun getStory(storyId: String?): Result<Story> {
        TODO("Not yet implemented")
    }

    override fun feedStory(): Flow<PagingData<Story>> {
        TODO("Not yet implemented")
    }

    override fun getMyStories(): Flow<List<Story>> {
        TODO("Not yet implemented")
    }

    override fun getUserStory(username: String): Flow<List<Story>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStory(storyId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun viewStory(storyId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun likeStory(storyId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unlikeStory(storyId: String) {
        TODO("Not yet implemented")
    }

    fun getStories(): Flow<PagingData<Story>> {
        return emptyFlow()
    }

//    suspend fun getUserStory(username: String): Flow<List<Story>> {
//        return flow {
//            val localStories = storyDao.getUserStory(username = username)
//            emit(localStories)
//
//            val remoteStories = try {
////                coreService.getUserStory(username = username)
//            } catch (e: Exception) {
//                if (e is CancellationException)
//                    throw e
//                e.printStackTrace()
//                null
//            }
//        }
//    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}