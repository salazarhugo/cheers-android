package com.salazar.cheers.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.salazar.cheers.data.db.entities.UserItem

@Dao
interface UserItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userItem: UserItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserItem>)

    @Query("SELECT * FROM user_item WHERE username LIKE '%' || :query || '%' ")
    suspend fun searchUser(query: String): List<UserItem>
}