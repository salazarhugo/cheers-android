package com.salazar.cheers.domain.search

import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.data.search.SearchRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateRecentUserUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        search: RecentSearch,
    ): Result<Unit, DataError.Local> = withContext(ioDispatcher) {
        return@withContext searchRepository.insertRecentSearch(
            search = search,
        )
    }
}
