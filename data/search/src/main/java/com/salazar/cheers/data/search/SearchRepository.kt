package com.salazar.cheers.data.search

import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.SearchResult
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.flow.Flow


interface SearchRepository {
    suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<SearchResult, DataError>

    suspend fun getRecentSearch(): Flow<List<RecentSearch>>

    suspend fun insertRecentSearch(search: RecentSearch): Result<Unit, DataError.Local>

    suspend fun deleteRecentSearch(search: RecentSearch): Result<Unit, DataError.Local>

}
