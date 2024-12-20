package com.salazar.cheers.domain.search

import com.salazar.cheers.core.model.SearchResult
import com.salazar.cheers.data.search.SearchRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
    ): Flow<Result<SearchResult, DataError>> = withContext(ioDispatcher) {
        return@withContext flowOf(searchRepository.search(
            query = query,
            page = page,
            pageSize = pageSize,
        ))
    }
}
