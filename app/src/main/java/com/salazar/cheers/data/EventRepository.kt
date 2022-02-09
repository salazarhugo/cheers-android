package com.salazar.cheers.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.internal.EventUi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(private val service: Neo4jService) {

    fun getEvents(): Flow<PagingData<EventUi>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = true),
            pagingSourceFactory = { EventsPagingSource(service) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}