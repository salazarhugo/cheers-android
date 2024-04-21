package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.FriendRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tickets: List<FriendRequestEntity>)

    @Query("DELETE FROM friend_requests")
    suspend fun clear()

    @Query("DELETE FROM friend_requests WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM friend_requests")
    fun listFriendRequests(): Flow<List<FriendRequestEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequests(friendRequests: List<FriendRequestEntity>) {
        clear()
        insert(friendRequests)
    }
}