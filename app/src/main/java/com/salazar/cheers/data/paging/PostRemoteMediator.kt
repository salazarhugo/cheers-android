package com.salazar.cheers.data.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.GoApi
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.entities.RemoteKey
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import com.salazar.cheers.internal.Post
import java.io.IOException
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val POST_STARTING_PAGE_INDEX = 0

class PostRemoteMediator(
    private val database: CheersDatabase,
    private val service: GoApi,
) : RemoteMediator<Int, Post>() {

    val postDao = database.postDao()
    val userDao = database.userDao()
    val remoteKeyDao = database.remoteKeyDao()
    val initialPage = 0

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

            if (loadType == LoadType.REFRESH) {
                remoteKeyDao.clear()
            }

            val prevKey = if (page == initialPage) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = response.map {
                RemoteKey(postId = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            remoteKeyDao.insertAll(keys)
            postDao.insertAll(response.map { it.copy(accountId = FirebaseAuth.getInstance().currentUser?.uid!!) })

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

//    override suspend fun initialize(): InitializeAction {
//        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
//        return if (System.currentTimeMillis() - database.lastUpdated() >= cacheTimeout)
//        {
//            // Cached data is up-to-date, so there is no need to re-fetch
//            // from the network.
//            InitializeAction.SKIP_INITIAL_REFRESH
//        } else {
//            // Need to refresh cached data from network; returning
//            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
//            // APPEND and PREPEND from running until REFRESH succeeds.
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        }
//    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Post>): RemoteKey? {
        return state.lastItemOrNull()?.let { postFeed ->
            database.withTransaction {
                remoteKeyDao.remoteKeyByPostId(postFeed.id)
            }
        }
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