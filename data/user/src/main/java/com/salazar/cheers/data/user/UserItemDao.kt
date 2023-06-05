package com.salazar.cheers.data.user

import androidx.room.*
import com.salazar.cheers.core.model.UserItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface UserItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userItem: com.salazar.cheers.core.model.UserItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<com.salazar.cheers.core.model.UserItem>)

    @Update
    suspend fun update(user: com.salazar.cheers.core.model.UserItem)

    @Query("SELECT * FROM user_item WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserItem(userIdOrUsername: String): Flow<com.salazar.cheers.core.model.UserItem>

    @Query("SELECT * FROM user_item")
    fun listUserItems(): Flow<List<UserItem>>

    @Query("SELECT * FROM user_item WHERE id IN (:ids)")
    fun listUsersIn(ids: List<String>): Flow<List<UserItem>>

    @Query("SELECT * FROM user_item WHERE username LIKE '%' || :query || '%' ")
    suspend fun searchUser(query: String): List<com.salazar.cheers.core.model.UserItem>

    @Transaction
    suspend fun deleteFriend(userId: String) {
        val user = getUserItem(userId).firstOrNull() ?: return
        update(user.copy(friend = false))
    }
}