package com.salazar.cheers.domain.usecase.get_party

import com.salazar.cheers.data.repository.PartyRepository
import com.salazar.cheers.internal.Party
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPartyUseCase @Inject constructor(
    private val repository: PartyRepository,
){
    operator fun invoke(partyId: String): Flow<Party> {
        return repository.getEvent(eventId = partyId)
    }
}