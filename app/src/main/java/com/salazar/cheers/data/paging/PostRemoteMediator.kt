package com.salazar.cheers.data.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.entities.RemoteKey
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import java.io.IOException
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val POST_STARTING_PAGE_INDEX = 0

class PostRemoteMediator(
    private val database: CheersDatabase,
    private val networkService: Neo4jService,
) : RemoteMediator<Int, PostFeed>() {

    val postDao = database.postDao()
    val remoteKeyDao = database.remoteKeyDao()
    val initialPage = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostFeed>
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
            val response = networkService.getPostFeed(page, NETWORK_PAGE_SIZE)

            when (response) {
                is Result.Success -> {
                    val result = response.data
                    val endOfPaginationReached = result.size < state.config.pageSize

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            remoteKeyDao.clear()
//                            postDao.clearAll()
                        }

                        val prevKey = if (page == initialPage) null else page - 1
                        val nextKey = if (endOfPaginationReached) null else page + 1
                        val keys = result.map {
                            RemoteKey(postId = it.first.id, prevKey = prevKey, nextKey = nextKey)
                        }

                        remoteKeyDao.insertAll(keys)
                        result.forEach {
                            postDao.insert(
                                it.first,//.copy(relativeTime = prettyDate(it.first.createdTime)),
                                it.second,
                            )
                        }
                    }
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                is Result.Error -> MediatorResult.Error(response.exception)
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
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

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostFeed>): RemoteKey? {
        return state.lastItemOrNull()?.let { postFeed ->
            database.withTransaction {
                remoteKeyDao.remoteKeyByPostId(postFeed.post.id)
            }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostFeed>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.post?.id?.let { id ->
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