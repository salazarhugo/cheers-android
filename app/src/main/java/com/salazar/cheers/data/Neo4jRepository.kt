package com.salazar.cheers.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.EventUi
import com.salazar.cheers.internal.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Neo4jRepository @Inject constructor(
    private val neo4JService: Neo4jService
){
    fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
//                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostsPagingSource(neo4JService) }
        ).flow
    }

    fun getEvents(): Flow<PagingData<EventUi>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
//                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { EventsPagingSource(neo4JService) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}