package com.salazar.cheers.domain.feed_party

import com.salazar.cheers.core.model.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.domain.get_current_city.GetCurrentCityFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ListPartyFeedFlowUseCase @Inject constructor(
    private val getCurrentCityUseCase: GetCurrentCityFlowUseCase,
    private val partyRepository: PartyRepository,
) {
    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = 10,
    ): Flow<List<Party>> {
        return getCurrentCityUseCase().flatMapLatest { city ->
            partyRepository.feedParty(
                city = city,
                page = page,
                pageSize = pageSize,
            )
        }
    }
}