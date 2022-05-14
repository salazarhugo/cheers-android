package com.salazar.cheers.data.paging

import android.util.Log
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.data.entities.StoryRemoteKey
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import retrofit2.HttpException
import java.io.IOException

class StoryRemoteMediator(
    private val database: CheersDatabase,
    private val networkService: Neo4jService,
) : RemoteMediator<Int, Story>() {

    private val storyDao = database.storyDao()
    private val userDao = database.userDao()
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
            val response = networkService.getStoryFeed(page, NETWORK_PAGE_SIZE)

            when (response) {
                is Result.Success -> {
                    val result = response.data
                    val endOfPaginationReached = result.size < state.config.pageSize
                    Log.d("HAHA", result.toString())

                    if (loadType == LoadType.REFRESH) {
                        remoteKeyDao.clear()
                        storyDao.clearAll()
                    }

                    val prevKey = if (page == initialPage) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = result.map {
                        StoryRemoteKey(storyId = it.first.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    remoteKeyDao.insertAll(keys)
                    result.forEach {
                        userDao.insertOrUpdateAll(it.second)
                        storyDao.insert(it.first)
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

    private suspend fun getStoryRemoteKeyForLastItem(state: PagingState<Int, Story>): StoryRemoteKey? {
        return state.lastItemOrNull()?.let { story ->
            database.withTransaction {
                remoteKeyDao.remoteKeyByStoryId(story.story.id)
            }
        }
    }

    private suspend fun getStoryRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): StoryRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.story?.id?.let { id ->
                database.withTransaction { remoteKeyDao.remoteKeyByStoryId(id) }
            }
        }
    }
}

