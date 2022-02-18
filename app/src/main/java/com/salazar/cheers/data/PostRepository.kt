package com.salazar.cheers.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.ui.add.Privacy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
        }.flow
    }

    fun getPostsWithAuthorId(authorId: String): List<PostFeed> =
        postDao.getPostsWithAuthorId(authorId = authorId)

    suspend fun getMapPosts(privacy: Privacy): List<PostFeed> {
        return postDao.getMapPosts(privacy = privacy).map {
            it.copy(
                tagUsers =
                postDao.getPostUsers(it.post.tagUsersId)
            )
        }
    }

    suspend fun getPost(postId: String): PostFeed {
        val post = postDao.getPost(postId = postId)
        return post.copy(tagUsers = postDao.getPostUsers(post.post.tagUsersId))
    }


    fun observeLikes(): Flow<Set<String>> = likes

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}