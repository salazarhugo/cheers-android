package com.salazar.cheers.domain.usecase.get_party

import com.salazar.cheers.internal.Party
import com.salazar.cheers.parties.data.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPartyUseCase @Inject constructor(
    private val repository: PartyRepository,
){
    suspend operator fun invoke(partyId: String): Flow<Party> {
        return repository.getParty(partyId = partyId)
    }
}