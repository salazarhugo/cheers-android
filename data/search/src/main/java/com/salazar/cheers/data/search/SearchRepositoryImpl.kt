package com.salazar.cheers.data.search

import cheers.search.v1.SearchRequest
import cheers.search.v1.SearchServiceGrpcKt
import cheers.type.Pagination
import com.salazar.cheers.core.db.dao.CheersDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.SearchResult
import com.salazar.cheers.shared.data.mapper.toSearchResult
import com.salazar.cheers.shared.data.toDataError
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val cheersDao: CheersDao,
    private val service: SearchServiceGrpcKt.SearchServiceCoroutineStub,
) : SearchRepository {
    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<SearchResult, DataError> {
        val pagination = Pagination.PaginationRequest.newBuilder()
            .setPage(page)
            .setPageSize(pageSize)
            .build()

        val request = SearchRequest.newBuilder()
            .setQuery(query)
            .setPagination(pagination)
            .build()

        return try {
            val response = service.search(request).toSearchResult()
            Result.Success(response)
        } catch (e: StatusException) {
            e.printStackTrace()
            Result.Error(e.toDataError())
        }
    }

    override suspend fun getRecentSearch(): Flow<List<RecentSearch>> {
        return cheersDao.getRecentSearches().map { it.map { it.asExternalModel() } }
    }

    override suspend fun insertRecentSearch(recentSearch: RecentSearch): Result<Unit, DataError.Local> {
        return try {
            cheersDao.insertRecentUser(recentSearch.asEntity())
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteRecentSearch(recentSearch: RecentSearch): Result<Unit, DataError.Local> {
        return try {
            cheersDao.deleteRecentUser(search = recentSearch.asEntity())
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
}