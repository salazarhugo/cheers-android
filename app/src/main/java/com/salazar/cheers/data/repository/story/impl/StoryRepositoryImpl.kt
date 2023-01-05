package com.salazar.cheers.data.repository.story.impl

import androidx.paging.PagingData
import cheers.story.v1.*
import cheers.type.StoryOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.StoryDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.mapper.toStory
import com.salazar.cheers.data.mapper.toUser
import com.salazar.cheers.data.repository.story.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val storyDao: StoryDao,
    private val database: CheersDatabase,
    private val service: StoryServiceGrpcKt.StoryServiceCoroutineStub,
) : StoryRepository {

    override suspend fun createStory(story: StoryOuterClass.Story): Result<Unit> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = CreateStoryRequest.newBuilder()
            .setStory(story)
            .build()

        return try {
            val response = service.createStory(request)
            storyDao.insert(response.toStory(uid, uid))
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getStory(storyId: String?): Result<Story> {
        TODO("Not yet implemented")
    }

    override suspend fun feedStory(
        page: Int,
        pageSize: Int
    ): Flow<List<UserWithStories>> {
        val skip = pageSize * (page - 1)
        return storyDao.feedStory(skip, pageSize)
            .map { userWithStories ->
                userWithStories
                    // Remove users with no stories
                    .filter { it.stories.isNotEmpty() }
                    // Sort by un-viewed story first
                    .sortedBy { it.stories.all { it.viewed } }
            }
    }

    override suspend fun fetchFeedStory(page: Int, pageSize: Int): Result<List<UserWithStories>> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = FeedStoryRequest.newBuilder()
            .setPageSize(pageSize)
            .setPage(page)
            .build()

        try {
            val response = service.feedStory(request)
            val remoteUserWithStoriesList = response.itemsList
            val userWithStoriesList = remoteUserWithStoriesList.map { userWithStories ->
                UserWithStories(
                    userWithStories.user.toUser(),
                    stories = userWithStories.storiesList.map {
                        it.toStory(
                            userWithStories.user.id,
                            uid
                        )
                    })
            }

            remoteUserWithStoriesList.forEach { userWithStories ->
                val user = userWithStories.user.toUser()
                val stories = userWithStories.storiesList.map {
                    it.toStory(
                        authorId = user.id,
                        accountId = uid
                    )
                }

                userDao.insert(user)
                storyDao.insertStories(stories)
            }

            return Result.success(userWithStoriesList)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getMyStories(): Flow<List<Story>> {
        TODO("Not yet implemented")
    }

    override fun getUserStory(username: String): Flow<List<Story>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStory(storyId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val request = DeleteStoryRequest.newBuilder()
            .setId(storyId)
            .build()

        return@withContext try {
            service.deleteStory(request)
            storyDao.deleteWithId(storyId = storyId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun viewStory(storyId: String): Result<Unit> {
        val story = storyDao.getStory(storyId)

        if (story?.viewed == true)
            return Result.success(Unit)

        storyDao.viewStory(storyId = storyId)
        val request = ViewStoryRequest.newBuilder()
            .setId(storyId)
            .build()

        return try {
            service.viewStory(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeStory(storyId: String): Result<Unit> {
        storyDao.likeStory(storyId = storyId)
        val request = LikeStoryRequest.newBuilder()
            .setId(storyId)
            .build()

        return try {
            service.likeStory(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikeStory(storyId: String): Result<Unit> {
        storyDao.unlikeStory(storyId = storyId)
        val request = UnlikeStoryRequest.newBuilder()
            .setId(storyId)
            .build()

        return try {
            service.unlikeStory(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getStories(): Flow<PagingData<Story>> {
        return emptyFlow()
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}