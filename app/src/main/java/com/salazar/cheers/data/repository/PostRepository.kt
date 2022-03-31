package com.salazar.cheers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.paging.PostRemoteMediator
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val service: Neo4jService,
    private val database: CheersDatabase
) {
    val postDao = database.postDao()
    private val likes = MutableStateFlow<Set<String>>(setOf())

    fun getPosts(): Flow<PagingData<PostFeed>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = PostRemoteMediator(database = database, networkService = service),
        ) {
            postDao.pagingSourceFeed()
        }.flow.map {
            it.map {
                it.copy(tagUsers = postDao.getPostUsers(it.post.tagUsersId))
            }
        }
    }

    fun profilePostFeed(userIdOrUsername: String): Flow<PagingData<PostFeed>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = PostRemoteMediator(database = database, networkService = service),
        ) {
            postDao.profilePostFeed(userIdOrUsername = userIdOrUsername)
        }.flow.map {
            it.map {
                it.copy(tagUsers = postDao.getPostUsers(it.post.tagUsersId))
            }
        }
    }

    suspend fun getPostsWithUsername(username: String): List<Post> {
        return postDao.getPostsWithUsername(username = username)
    }

    suspend fun getPostsWithAuthorId(authorId: String): List<Post> {
        return postDao.getPostsWithAuthorId(authorId = authorId)
    }

    suspend fun getMapPosts(privacy: Privacy): List<PostFeed> {
        return postDao.getMapPosts(privacy = privacy).map {
            it.copy(tagUsers = postDao.getPostUsers(it.post.tagUsersId))
        }
    }

    suspend fun getPost(postId: String): PostFeed {
        val post = postDao.getPost(postId = postId)
        return post.copy(tagUsers = postDao.getPostUsers(post.post.tagUsersId))
    }

    suspend fun toggleLike(post: Post) {
        val likes = if (post.liked) post.likes - 1 else post.likes + 1

        postDao.update(post.copy(liked = !post.liked, likes = likes))

        if (post.liked)
            service.unlikePost(post.id)
        else
            service.likePost(post.id)
    }


    fun observeLikes(): Flow<Set<String>> = likes

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}