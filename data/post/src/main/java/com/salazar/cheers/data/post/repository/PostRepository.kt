package com.salazar.cheers.data.post.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import cheers.post.v1.CreatePostRequest
import cheers.post.v1.DeletePostRequest
import cheers.post.v1.FeedPostRequest
import cheers.post.v1.LikePostRequest
import cheers.post.v1.ListPostLikesRequest
import cheers.post.v1.ListPostRequest
import cheers.post.v1.PostServiceGrpcKt
import cheers.post.v1.UnlikePostRequest
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.shared.data.mapper.toUserItem
import com.salazar.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val postService: PostServiceGrpcKt.PostServiceCoroutineStub,
    private val postDao: PostDao,
    private val workManager: WorkManager,
) {
    suspend fun getPostFeed(page: Int, pageSize: Int): Result<List<Post>> {
        val request = FeedPostRequest.newBuilder()
            .setPageSize(pageSize)
            .setPage(page)
            .build()

        return try {
            val remotePosts = postService.feedPost(request)
            val posts = remotePosts.postsList.map { it.toPost() }
            postDao.insertAll(posts)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPostFeedFlow(): Flow<List<Post>> {
        return postDao.getPostFeed()
    }

    suspend fun updatePost(post: Post) {
        postDao.update(post = post)
    }

    suspend fun listPost(userIdOrUsername: String): Flow<List<Post>> {
        return flow {
            emit(postDao.getUserPosts(userIdOrUsername).first())

            val remoteUserPosts = try {
                val request = ListPostRequest.newBuilder()
                    .setPage(1)
                    .setPageSize(9)
                    .setUsername(userIdOrUsername)
                    .build()

                val response = postService.listPost(request)
                val posts = response.postsList.map {
                    it.toPost()
                }
                posts
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            remoteUserPosts?.let {
                postDao.insertUserPosts(it)
                emitAll(postDao.getUserPosts(userIdOrUsername))
            }
        }
    }

    suspend fun listPostLikes(postID: String): Flow<List<UserItem>> {
        return flow {
            val remoteUserPosts = try {
                val request = ListPostLikesRequest.newBuilder()
                    .setPage(0)
                    .setPageSize(10)
                    .setPostId(postID)
                    .build()

                val response = postService.listPostLikes(request)
                val users = response.usersList.map {
                    it.toUserItem()
                }
                users
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            remoteUserPosts?.let { users ->
                emit(users)
            }
        }
    }

    suspend fun likePost(postId: String) {
        val request = LikePostRequest.newBuilder()
            .setPostId(postId)
            .build()

        try {
            postService.likePost(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun unlikePost(postId: String) {
        val request = UnlikePostRequest.newBuilder()
            .setPostId(postId)
            .build()

        try {
            postService.unlikePost(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun uploadPost(vararg pairs: Pair<String, Any?>) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest =
            OneTimeWorkRequestBuilder<CreatePostWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(*pairs))
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniqueWork(
            "post_upload",
            ExistingWorkPolicy.REPLACE,
            uploadWorkRequest,
        )
    }

    suspend fun createPost(
        request: CreatePostRequest,
    ): Resource<Unit> {
        println("HUGO ${request.drinkId}")
        val response = try {
            postService.createPost(request)
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(e.localizedMessage)
        }

        val post = response.toPost()
        postDao.insert(post)

        return Resource.Success(Unit)
    }

    suspend fun getPostsWithAuthorId(authorId: String): List<Post> {
        return postDao.getPostsWithAuthorId(authorId = authorId)
    }

    suspend fun clearPosts() {
        postDao.clearAll()
    }

    suspend fun deletePost(postId: String) {
        postDao.deleteWithId(postId)
        try {
            val request = DeletePostRequest.newBuilder()
                .setId(postId)
                .build()
            postService.deletePost(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getPostMembers(postId: String) = withContext(Dispatchers.IO) {
        return@withContext try {
//            coreService.postMembers(postId = postId, pageSize = 20, page = 0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun listMapPost(privacy: Privacy): Flow<List<Post>> {
        return postDao.listMapPost(privacy = privacy)
    }

    fun postFlow(postId: String) = postDao.postFlow(postId = postId)

    suspend fun getPost(postId: String): Post? {
        return postDao.getPost(postId = postId)
    }

    suspend fun toggleLike(post: Post) = withContext(Dispatchers.IO) {
        val likes = if (post.liked) post.likes - 1 else post.likes + 1

        postDao.update(post.copy(liked = !post.liked, likes = likes))

        if (post.liked)
            unlikePost(postId = post.id)
        else
            likePost(postId = post.id)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}