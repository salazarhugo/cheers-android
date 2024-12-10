package com.salazar.cheers.domain.search

import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.data.search.SearchRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRecentSearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Flow<List<RecentSearch>> = withContext(ioDispatcher) {
        return@withContext searchRepository.getRecentSearch()
    }
}
