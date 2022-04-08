package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.salazar.cheers.internal.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Transaction
    @Query("SELECT * FROM users")
    fun pagingSource(): PagingSource<Int, User>

    @Query("SELECT * FROM users WHERE users.id = :userId")
    suspend fun getUser(userId: String): User

    @Query("SELECT * FROM users WHERE users.id = :userId")
    suspend fun getUserNullable(userId: String): User?

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' ")
    fun queryUsers(query: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    suspend fun getUserWithUsername(userIdOrUsername: String): User

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserFlowWithUsername(userIdOrUsername: String): Flow<User>

    @Query("SELECT users.id FROM users WHERE users.username = :username")
    suspend fun getUserIdWithUsername(username: String): String

    @Query("SELECT * FROM users WHERE users.id IN (:ids)")
    suspend fun getUsersWithListOfIds(ids: List<String>): List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    suspend fun insertOrUpdate(user: User) {
        if (insert(user = user) == -1L)
            update(user = user)
    }

    suspend fun insertOrUpdateAll(users: List<User>) {
        users.forEach {
            insertOrUpdate(user = it)
        }
    }

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteWithId(userId: String)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}