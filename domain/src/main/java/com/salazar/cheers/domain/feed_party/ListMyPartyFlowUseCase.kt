package com.salazar.cheers.domain.feed_party

import com.salazar.cheers.core.model.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.domain.get_account.GetAccountIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ListMyPartyFlowUseCase @Inject constructor(
    private val getAccountIdUseCase: GetAccountIdUseCase,
    private val partyRepository: PartyRepository,
) {
    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = 10,
    ): Flow<List<Party>> {
        val userID = getAccountIdUseCase().firstOrNull() ?: return emptyFlow()

        return partyRepository.listPartyFlow(
            filter = "hosting",
            userId = userID,
            page = page,
            pageSize = pageSize,
        )
    }
}