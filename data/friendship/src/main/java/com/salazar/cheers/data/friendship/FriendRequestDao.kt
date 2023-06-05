package com.salazar.cheers.data.friendship

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tickets: List<FriendRequest>)

    @Query("DELETE FROM friend_requests")
    suspend fun clear()

    @Query("DELETE FROM friend_requests WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM friend_requests")
    fun listFriendRequests(): Flow<List<FriendRequest>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequests(friendRequests: List<FriendRequest>) {
        clear()
        insert(friendRequests)
    }
}