package com.salazar.cheers.data.db

import androidx.room.*
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.db.entities.UserSuggestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface UserItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userItem: UserItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserItem>)

    @Update
    suspend fun update(user: UserItem)

    @Query("SELECT * FROM user_item WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserItem(userIdOrUsername: String): Flow<UserItem>

    @Query("SELECT * FROM user_item")
    fun listUserItems(): Flow<List<UserItem>>

    @Query("SELECT * FROM user_item WHERE username LIKE '%' || :query || '%' ")
    suspend fun searchUser(query: String): List<UserItem>

    @Transaction
    suspend fun deleteFriend(userId: String)  {
        val user = getUserItem(userId).firstOrNull() ?: return
        update(user.copy(friend = false))
    }
}