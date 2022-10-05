package com.salazar.cheers.data.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.entities.EventRemoteKey
import com.salazar.cheers.data.repository.PostRepository.Companion.NETWORK_PAGE_SIZE
import com.salazar.cheers.internal.Party
import java.io.IOException

private const val POST_STARTING_PAGE_INDEX = 0

class EventRemoteMediator(
    private val database: CheersDatabase,
    private val service: CoreService,
) : RemoteMediator<Int, Party>() {

    private val eventDao = database.partyDao()
    val userDao = database.userDao()
    private val remoteKeyDao = database.eventRemoteKeyDao()
    private val initialPage = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Party>
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

            val result = service.getPartyFeed(page, NETWORK_PAGE_SIZE)
            val endOfPaginationReached = result.size < state.config.pageSize

            if (loadType == LoadType.REFRESH) {
                eventDao.clearAll()
                remoteKeyDao.clear()
            }

            val prevKey = if (page == initialPage) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = result.map {
                EventRemoteKey(eventId = it.id, prevKey = prevKey, nextKey = nextKey)
            }

            remoteKeyDao.insertAll(keys)
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!
            eventDao.insertAll(result.map { it.copy(accountId = uid) })

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            e.printStackTrace()
            MediatorResult.Error(e)
        } catch (e: Exception) {
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Party>): EventRemoteKey? {
        return state.lastItemOrNull()?.let { event ->
            database.withTransaction {
                remoteKeyDao.remoteKeyByEventId(event.id)
            }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Party>): EventRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.withTransaction { remoteKeyDao.remoteKeyByEventId(id) }
            }
        }
    }
}