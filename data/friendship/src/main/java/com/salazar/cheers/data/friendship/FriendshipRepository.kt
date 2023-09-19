package com.salazar.cheers.data.friendship

import com.salazar.cheers.core.model.UserItem
import com.salazar.common.util.Resource
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
    suspend fun listFriend(): Flow<List<String>>

    /**
     * List friend request.
     */
    suspend fun listFriendRequest(): Flow<List<FriendRequest>>

    /**
     * Fetch friend request from remote.
     */
    suspend fun fetchFriendRequest(userId: String): Result<List<UserItem>>

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