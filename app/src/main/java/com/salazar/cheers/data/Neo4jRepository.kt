package com.salazar.cheers.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.EventUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Neo4jRepository @Inject constructor(
    private val neo4JService: Neo4jService,
    private val database: CheersDatabase
){
    val postDao = database.postDao()
    private val likes = MutableStateFlow<Set<String>>(setOf())

    @OptIn(ExperimentalPagingApi::class)
    fun getPosts(): Flow<PagingData<PostFeed>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = PostRemoteMediator(database = database, networkService = neo4JService),
        ) {
            postDao.pagingSourceFeed()
        }.flow
    }

    fun getEvents(): Flow<PagingData<EventUi>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = { EventsPagingSource(neo4JService) }
        ).flow
    }

    fun observeLikes(): Flow<Set<String>> = likes

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}