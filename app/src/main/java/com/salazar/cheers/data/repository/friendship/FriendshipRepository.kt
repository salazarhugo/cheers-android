package com.salazar.cheers.data.repository.friendship

import com.salazar.common.util.Resource
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.friendship.domain.models.FriendRequest
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Comment data layer.
 */
interface FriendshipRepository {

    /**
     * Create a friend request.
     */
    suspend fun createFriendRequest(userId: String): Result<Unit>

    /**
     * List friends.
     */
    suspend fun listFriend(userId: String): Flow<Resource<List<com.salazar.cheers.core.model.UserItem>>>

    /**
     * List friend request.
     */
    suspend fun listFriendRequest(): Flow<List<FriendRequest>>

    /**
     * Fetch friend request from remote.
     */
    suspend fun fetchFriendRequest(): Flow<Resource<List<com.salazar.cheers.core.model.UserItem>>>

    /**
     * Accept friend request.
     */
    suspend fun acceptFriendRequest(userId: String): Result<Unit>

    /**
     * Refuse friend request.
     */
    suspend fun cancelFriendRequest(userId: String): Result<Unit>

    /**
     * Remove a friend.
     */
    suspend fun removeFriend(userId: String): Result<Unit>

    /**
     * Get friend request count.
     */
    suspend fun getFriendRequestCount(): Flow<Int>
}