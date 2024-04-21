package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.UserItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface UserItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userItem: UserItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserItemEntity>)

    @Update
    suspend fun update(user: UserItemEntity)

    @Query("SELECT * FROM user_item WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserItem(userIdOrUsername: String): Flow<UserItemEntity>

    @Query("SELECT * FROM user_item")
    fun listUserItems(): Flow<List<UserItemEntity>>

    @Query("SELECT * FROM user_item WHERE id IN (:ids)")
    fun listUsersIn(ids: List<String>): Flow<List<UserItemEntity>>

    @Query("SELECT * FROM user_item WHERE username LIKE '%' || :query || '%' ")
    suspend fun searchUser(query: String): List<UserItemEntity>

    @Transaction
    suspend fun deleteFriend(userId: String) {
        val user = getUserItem(userId).firstOrNull() ?: return
        update(user.copy(friend = false))
    }
}