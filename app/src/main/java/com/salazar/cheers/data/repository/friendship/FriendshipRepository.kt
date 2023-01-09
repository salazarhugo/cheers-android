package com.salazar.cheers.data.repository.friendship

import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.User
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
    suspend fun listFriend(): Flow<Resource<List<UserItem>>>

    /**
     * List friend request.
     */
    suspend fun listFriendRequest(): Flow<Resource<List<UserItem>>>

    /**
     * Accept friend request.
     */
    suspend fun acceptFriendRequest(userId: String): Result<Unit>

    /**
     * Refuse friend request.
     */
    suspend fun cancelFriendRequest(userId: String): Result<Unit>
}