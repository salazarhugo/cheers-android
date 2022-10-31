package com.salazar.cheers.data.repository.party

import cheers.type.PartyOuterClass
import cheers.type.StoryOuterClass
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.Party
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
    fun getMyParties(): Flow<List<Party>>

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
}