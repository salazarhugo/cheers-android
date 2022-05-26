package com.salazar.cheers.data.paging

import android.util.Log
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.salazar.cheers.backend.GoApi
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.entities.StoryRemoteKey
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import retrofit2.HttpException
import java.io.IOException

class StoryRemoteMediator(
    private val database: CheersDatabase,
    private val networkService: GoApi,
) : RemoteMediator<Int, Story>() {

    private val storyDao = database.storyDao()
    private val remoteKeyDao = database.storyRemoteKeyDao()
    private val initialPage = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>
    ): MediatorResult {

        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getStoryRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: initialPage
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getStoryRemoteKeyForLastItem(state)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKeys.nextKey ?: return MediatorResult.Success(true)
                }
            }
            val response = networkService.storyFeed(page, NETWORK_PAGE_SIZE)
            val endOfPaginationReached = response.size < state.config.pageSize

            if (loadType == LoadType.REFRESH) {
                remoteKeyDao.clear()
                storyDao.clearAll()
            }

            val prevKey = if (page == initialPage) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = response.map {
                StoryRemoteKey(storyId = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            remoteKeyDao.insertAll(keys)
            storyDao.insertAll(response)

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getStoryRemoteKeyForLastItem(state: PagingState<Int, Story>): StoryRemoteKey? {
        return state.lastItemOrNull()?.let { story ->
            database.withTransaction {
                remoteKeyDao.remoteKeyByStoryId(story.id)
            }
        }
    }

    private suspend fun getStoryRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): StoryRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.withTransaction { remoteKeyDao.remoteKeyByStoryId(id) }
            }
        }
    }
}

