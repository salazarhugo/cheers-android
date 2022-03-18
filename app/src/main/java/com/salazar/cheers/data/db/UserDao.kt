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

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    suspend fun getUserWithUsername(userIdOrUsername: String): User

    @Query("SELECT users.id FROM users WHERE users.username = :username")
    suspend fun getUserIdWithUsername(username: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteWithId(userId: String)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}