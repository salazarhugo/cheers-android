package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.salazar.cheers.internal.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun pagingSource(): PagingSource<Int, User>

    @Query("SELECT * FROM users WHERE users.id = :userId")
    suspend fun getUser(userId: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}