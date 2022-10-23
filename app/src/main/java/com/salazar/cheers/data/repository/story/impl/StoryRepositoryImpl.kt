package com.salazar.cheers.data.repository.story.impl

import android.util.Log
import cheers.story.v1.*
import androidx.paging.PagingData
import androidx.room.withTransaction
import cheers.post.v1.FeedPostRequest
import cheers.type.StoryOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.repository.story.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.salazar.cheers.data.db.StoryDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.mapper.toPost
import com.salazar.cheers.data.mapper.toStory
import com.salazar.cheers.data.mapper.toUser
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.story.fakeUsersWIthStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val storyDao: StoryDao,
    private val database: CheersDatabase,
    private val service: StoryServiceGrpcKt.StoryServiceCoroutineStub,
): StoryRepository {

    override suspend fun createStory(story: StoryOuterClass.Story): Result<Unit> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = CreateStoryRequest.newBuilder()
            .setStory(story)
            .build()

        return try {
            val response = service.createStory(request)
            storyDao.insert(response.toStory(uid))
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getStory(storyId: String?): kotlin.Result<Story> {
        TODO("Not yet implemented")
    }

    override suspend fun feedStory(
        page: Int,
        pageSize: Int
    ): Result<List<UserWithStories>> = withContext(Dispatchers.IO) {
        val request = FeedStoryRequest.newBuilder()
            .setPageSize(pageSize)
            .setPage(page)
            .build()

        val response = service.feedStory(request)
        val remoteUserWithStoriesList = response.itemsList

        Log.d("GRPC", remoteUserWithStoriesList.toString())
        remoteUserWithStoriesList.forEach { userWithStories ->
            val user = userWithStories.user.toUser()
            val stories = userWithStories.storiesList.map { it.toStory(user.id) }

//            database.withTransaction {
                userDao.insert(user)
                storyDao.insertAll(stories)
//            }
        }

        val userWithStoriesList = storyDao.feedStory()

        return@withContext Result.success(userWithStoriesList)
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

    override suspend fun viewStory(storyId: String): Result<Unit> {
        storyDao.viewStory(storyId = storyId)
        val request = ViewStoryRequest.newBuilder()
            .setId(storyId)
            .build()

        return try {
            service.viewStory(request)
            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
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

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}