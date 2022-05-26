package com.salazar.cheers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.salazar.cheers.backend.GoApi
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.paging.PostRemoteMediator
import com.salazar.cheers.data.repository.StoryRepository.Companion.NETWORK_PAGE_SIZE
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val goApi: GoApi,
    private val service: Neo4jService,
    private val database: CheersDatabase
) {
    val postDao = database.postDao()

    fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = PostRemoteMediator(database = database, service = goApi),
        ) {
            postDao.pagingSourceFeed()
        }.flow
    }

    fun profilePost(userIdOrUsername: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = PostRemoteMediator(database = database, service = goApi),
        ) {
            postDao.profilePost(userIdOrUsername = userIdOrUsername)
        }.flow
    }

    suspend fun addPost(post: Post) {
        try {
            goApi.createPost(post = post)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun getPostsWithUsername(username: String): List<Post> {
        return postDao.getPostsWithUsername(username = username)
    }

    suspend fun getPostsWithAuthorId(authorId: String): List<Post> {
        return postDao.getPostsWithAuthorId(authorId = authorId)
    }

    suspend fun deletePost(postId: String) {
        try {
            goApi.deletePost(postId = postId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

        suspend fun getMapPosts(privacy: Privacy): List<Post> {
            return postDao.getMapPosts(privacy = privacy)
//            .map {
//            it.copy(tagUsers = postDao.getPostUsers(it.post.tagUsersId))
//        }
        }

        suspend fun getPost(postId: String): Post {
            val post = postDao.getPost(postId = postId)
            return post.copy()
        }

        suspend fun toggleLike(post: Post) = withContext(Dispatchers.IO) {
            val likes = if (post.liked) post.likes - 1 else post.likes + 1

            postDao.update(post.copy(liked = !post.liked, likes = likes))

            if (post.liked)
                goApi.unlikePost(postId = post.id)
            else
                goApi.likePost(postId = post.id)
        }

        companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
    }
}