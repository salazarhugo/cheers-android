package com.salazar.cheers.domain.feed_party

import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListPartyUseCase @Inject constructor(
    private val partyRepository: PartyRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        filters: List<Filter> = emptyList(),
        page: Int = 1,
        pageSize: Int = 10,
    ): Result<Pair<List<Party>, List<Filter>>, DataError> = withContext(dispatcher) {
        val selectedFilterID = filters.firstOrNull { it.selected }?.id.orEmpty()
        return@withContext partyRepository.listParty(
            filter = selectedFilterID,
            page = page,
            pageSize = pageSize,
        )
    }
}