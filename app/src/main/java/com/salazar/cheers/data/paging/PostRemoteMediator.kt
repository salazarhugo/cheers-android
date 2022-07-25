package com.salazar.cheers.data.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.entities.RemoteKey
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import com.salazar.cheers.internal.Post
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val POST_STARTING_PAGE_INDEX = 0

class PostRemoteMediator(
    private val database: CheersDatabase,
    private val service: CoreService,
) : RemoteMediator<Int, Post>() {

    val postDao = database.postDao()
    private val remoteKeyDao = database.remoteKeyDao()
    private val initialPage = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Post>
    ): MediatorResult {

        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: initialPage
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKeys.nextKey ?: return MediatorResult.Success(true)
                }
            }

            val response = service.postFeed(page, NETWORK_PAGE_SIZE)
            val endOfPaginationReached = response.size < state.config.pageSize

            val prevKey = if (page == initialPage) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clear()
                    postDao.clearAll()
                }
                val keys = response.map { post ->
                    RemoteKey(
                        postId = post.id,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
                remoteKeyDao.insertAll(remoteKeys = keys)
                postDao.insertAll(response.map { it.copy(accountId = FirebaseAuth.getInstance().currentUser?.uid!!) })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Post>
    ): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { post ->
                remoteKeyDao.remoteKeyByPostId(postId = post.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Post>
    ): RemoteKey? {
        val lastPost = state.lastItemOrNull() ?: return null
        return remoteKeyDao.remoteKeyByPostId(postId = lastPost.id)
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Post>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.withTransaction { remoteKeyDao.remoteKeyByPostId(id) }
            }
        }
    }

    private fun prettyDate(str: String): String {
        val now = ZonedDateTime.now()
        val date = ZonedDateTime.parse(str)
        val diff = ChronoUnit.SECONDS.between(date, now)

        return if (diff < 60)
            "${diff}s"
        else if (diff < 60 * 60)
            "${diff / 60}m"
        else if (diff < 60 * 60 * 24)
            "${diff / 60 / 60}h"
        else if (diff < 60 * 60 * 24 * 7)
            "${diff / 60 / 60 / 24}d"
        else if (diff < 60 * 60 * 24 * 30)
            "${diff / 60 / 60 / 24 / 7}w"
        else if (diff < 60 * 60 * 24 * 30 * 12)
            "${diff / 60 / 60 / 24 / 30}M"
        else
            "+"
    }
}
