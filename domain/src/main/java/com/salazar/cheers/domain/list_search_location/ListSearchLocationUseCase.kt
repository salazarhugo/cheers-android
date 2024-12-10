package com.salazar.cheers.domain.list_search_location

import com.salazar.cheers.core.model.SearchSuggestion
import com.salazar.cheers.data.map.MapRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListSearchLocationUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        query: String,
    ): Result<List<SearchSuggestion>, DataError> = withContext(dispatcher) {
        return@withContext mapRepository.listSearchLocation(
            query = query,
        )
    }
}