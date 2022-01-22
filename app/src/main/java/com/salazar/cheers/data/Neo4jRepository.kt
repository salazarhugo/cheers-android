package com.salazar.cheers.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.EventUi
import com.salazar.cheers.internal.Post
import com.salazar.cheers.util.addOrRemove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Neo4jRepository @Inject constructor(
    private val neo4JService: Neo4jService
){
    private val likes = MutableStateFlow<Set<String>>(setOf())

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

    fun observeLikes(): Flow<Set<String>> = likes

    suspend fun toggleLikes(postId: String) {
        val set = likes.value.toMutableSet()
        set.addOrRemove(postId)
        likes.value = set
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}