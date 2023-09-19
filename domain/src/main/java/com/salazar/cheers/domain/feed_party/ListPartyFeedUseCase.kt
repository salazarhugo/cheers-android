package com.salazar.cheers.domain.feed_party

import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListPartyFeedUseCase @Inject constructor(
    private val partyRepository: PartyRepository,
) {
    suspend operator fun invoke(): Flow<List<Party>> {
        return partyRepository.feedParty(page = 0, pageSize = 10)
    }
}