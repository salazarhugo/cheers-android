package com.salazar.cheers.data.party.data.repository

import cheers.party.v1.CreatePartyRequest
import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.PartyID
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.shared.util.result.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Interface to the Story data layer.
 */
interface PartyRepository {
    val filtersFlow: MutableStateFlow<String>

    fun updateFilter(chatFilter: String): Unit

    /**
     * Create a Cheers party.
     */
    suspend fun createParty(request: CreatePartyRequest): Result<Unit>

    /**
     * Get a specific Cheers party.
     */
    suspend fun getParty(partyId: String): Flow<Party>

    /**
     * Get party feed from local database.
     */
    suspend fun feedParty(
        city: String,
        page: Int,
        pageSize: Int,
    ): Flow<List<Party>>

    /**
     * Fetch party feed from backend.
     */
    suspend fun fetchFeedParty(
        city: String,
        page: Int,
        pageSize: Int,
    ): Result<List<Party>>

    suspend fun listInterested(
        page: Int = 1,
        pageSize: Int = 10,
        partyID: PartyID,
    ): com.salazar.cheers.shared.util.result.Result<List<UserItem>, DataError>

    suspend fun listGoing(
        page: Int = 1,
        pageSize: Int = 10,
        partyID: PartyID,
    ): com.salazar.cheers.shared.util.result.Result<List<UserItem>, DataError>

    /**
     */
    suspend fun listParty(
        filter: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): com.salazar.cheers.shared.util.result.Result<Pair<List<Party>, List<Filter>>, DataError>

    /**
     */
    fun listPartyFlow(
        filter: String,
        page: Int = 1,
        pageSize: Int = 10,
        userId: String,
    ): Flow<List<Party>>


    /**
     * Delete a specific Cheers party.
     */
    suspend fun deleteParty(partyId: String): Result<Unit>

    /**
     * Interest a specific Cheers party.
     */
    suspend fun interestParty(partyId: String): Result<Unit>

    /**
     * Uninterest a specific Cheers party.
     */
    suspend fun uninterestParty(partyId: String): Result<Unit>

    suspend fun setWatchStatus(partyId: String, watchStatus: WatchStatus): Result<Unit>
}