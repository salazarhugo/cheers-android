package com.salazar.cheers.parties.domain.usecase.get_party

import com.salazar.cheers.core.model.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPartyUseCase @Inject constructor(
    private val repository: PartyRepository,
){
    suspend operator fun invoke(partyId: String): Flow<Party> {
        return repository.getParty(partyId = partyId)
    }
}