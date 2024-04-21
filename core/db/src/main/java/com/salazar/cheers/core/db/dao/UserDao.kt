package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.UserEntity
import com.salazar.cheers.core.db.model.UserSuggestionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface UserDao {
    @Query("SELECT * FROM user_suggestion")
    fun getUserSuggestions(): Flow<List<UserSuggestionEntity>>

    @Query("SELECT * FROM user_suggestion WHERE username = :username")
    suspend fun getUserSuggestion(username: String): UserSuggestionEntity?

    @Transaction
    suspend fun deleteFriend(userId: String) {
        val user = getUserFlow(userId).firstOrNull() ?: return
        update(user.copy(friend = false))
    }

    @Query("SELECT * FROM users WHERE users.id = :userIdOrUsername OR username = :userIdOrUsername")
    suspend fun getUser(userIdOrUsername: String): UserEntity

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    suspend fun getUserWithUsername(userIdOrUsername: String): UserEntity

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserFlow(userIdOrUsername: String): Flow<UserEntity?>

    @Query("SELECT users.id FROM users WHERE users.username = :username")
    suspend fun getUserIdWithUsername(username: String): String

    @Query("SELECT * FROM users WHERE users.id IN (:ids)")
    suspend fun getUsersWithListOfIds(ids: List<String>): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestions(users: List<UserSuggestionEntity>)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteWithId(userId: String)

    @Update
    suspend fun update(user: UserSuggestionEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("DELETE FROM user_suggestion")
    suspend fun clearSuggestions()

    @Query("DELETE FROM users")
    suspend fun clearAll()
}