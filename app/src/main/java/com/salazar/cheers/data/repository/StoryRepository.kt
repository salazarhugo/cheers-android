package com.salazar.cheers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.Story
import com.salazar.cheers.data.paging.StoryRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val service: Neo4jService,
    private val database: CheersDatabase
) {

    private val storyDao = database.storyDao()

    fun getStories(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true,
            ),
            remoteMediator = StoryRemoteMediator(database = database, networkService = service),
        ) {
            storyDao.pagingSource()
        }.flow
    }

    suspend fun seenStory(storyId: String) {
        service.seenStory(storyId = storyId)
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}