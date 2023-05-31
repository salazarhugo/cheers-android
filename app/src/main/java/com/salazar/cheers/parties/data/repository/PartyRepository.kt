package com.salazar.cheers.parties.data.repository

import cheers.type.PartyOuterClass
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.data.internal.WatchStatus
import com.salazar.cheers.data.db.entities.Story
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Story data layer.
 */
interface PartyRepository {

    /**
     * Create a Cheers party.
     */
    suspend fun createParty(party: PartyOuterClass.Party): Result<Unit>

    /**
     * Get a specific Cheers party.
     */
    suspend fun getParty(partyId: String): Flow<Party>

    /**
     * Get party feed from local database.
     */
    suspend fun feedParty(page: Int, pageSize: Int): Flow<List<Party>>

    /**
     * Fetch party feed from backend.
     */
    suspend fun fetchFeedParty(page: Int, pageSize: Int): Result<List<Party>>

    /**
     * Get current user stories.
     */
    fun listParty(
        page: Int = 1,
        pageSize: Int = 10,
        userId: String,
    ): Flow<List<Party>>

    /**
     * Get stories of a specific user.
     */
    fun getUserParty(username: String): Flow<List<Story>>

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