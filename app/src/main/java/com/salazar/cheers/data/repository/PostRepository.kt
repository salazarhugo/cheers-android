package com.salazar.cheers.data.repository

import cheers.post.v1.*
import cheers.type.PostOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.mapper.toPost
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
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
    private val database: CheersDatabase
) {
    val postDao = database.postDao()

    suspend fun getPostFeed(page: Int, pageSize: Int): Result<List<Post>> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = FeedPostRequest.newBuilder()
            .setPageSize(pageSize)
            .setPage(page)
            .build()

        return try {
            val remotePosts = postService.feedPost(request)
            val posts = remotePosts.postsList.map { it.toPost(uid) }
            postDao.insertAll(posts)
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPostFeedFlow(): Flow<List<Post>> {
        return postDao.getPostFeed()
    }

    suspend fun listPost(userIdOrUsername: String): Flow<List<Post>> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        return flow {
            emit(postDao.getUserPosts(userIdOrUsername).first())

            val remoteUserPosts = try {
                val request = ListPostRequest.newBuilder()
                    .setPage(0)
                    .setPageSize(9)
                    .setUsername(userIdOrUsername)
                    .build()

                val response = postService.listPost(request)
                val posts = response.postsList.map {
                    it.toPost(accountId = uid)
                }
                posts
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            remoteUserPosts?.let {
                postDao.insertUserPosts(uid, it)
                emitAll(postDao.getUserPosts(userIdOrUsername))
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

    suspend fun commentPost(comment: Comment) {
        try {
//            coreService.postComment(comment = comment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createPost(post: PostOuterClass.Post): Resource<Unit> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!

        val request = CreatePostRequest.newBuilder()
            .setPost(post)
            .build()

        val response = try {
            postService.createPost(request)
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(e.localizedMessage)
        }

        postDao.insert(response.toPost(uid))

        return Resource.Success(Unit)
    }

    suspend fun getUserPosts(): List<Post> {
        try {
//            val posts = coreService.getPosts()
//            postDao.insertAll(posts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return postDao.getPosts()
    }

    suspend fun getPostsWithAuthorId(authorId: String): List<Post> {
        return postDao.getPostsWithAuthorId(authorId = authorId)
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

    suspend fun getMapPosts(privacy: Privacy): List<Post> {
        return postDao.getMapPosts(privacy = privacy)
    }

    suspend fun postFlow(postId: String) = postDao.postFlow(postId = postId)

    suspend fun getPost(postId: String): Post {
        val post = postDao.getPost(postId = postId)
        return post.copy()
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