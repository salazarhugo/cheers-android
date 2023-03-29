package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.db.entities.UserSuggestion
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface UserDao {

    @Transaction
    @Query("SELECT * FROM users")
    fun pagingSource(): PagingSource<Int, User>

    @Query("SELECT * FROM activity WHERE accountId = :accountId")
    suspend fun getActivity(accountId: String = Firebase.auth.currentUser?.uid!!): List<Activity>

    @Query("SELECT * FROM user_suggestion WHERE accountId = :accountId")
    fun getUserSuggestions(accountId: String = Firebase.auth.currentUser?.uid!!): Flow<List<UserSuggestion>>

    @Query("SELECT * FROM user_suggestion WHERE username = :username")
    suspend fun getUserSuggestion(username: String): UserSuggestion?

    @Transaction
    suspend fun deleteFriend(userId: String)  {
        val user = getUserNullable(userId).firstOrNull() ?: return
        update(user.copy(friend = false))
    }

    @Query("SELECT * FROM users WHERE users.id = :userIdOrUsername OR username = :userIdOrUsername")
    suspend fun getUser(userIdOrUsername: String): User

    @Query("SELECT * FROM users WHERE users.id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserNullable(userIdOrUsername: String): Flow<User>

    @Query("SELECT * FROM users WHERE friend = 1")
    suspend fun getFriends(): List<User>

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    suspend fun getUserWithUsername(userIdOrUsername: String): User

    @Query("SELECT * FROM users WHERE id = :userIdOrUsername OR username = :userIdOrUsername")
    fun getUserFlow(userIdOrUsername: String): Flow<User>

    @Query("SELECT users.id FROM users WHERE users.username = :username")
    suspend fun getUserIdWithUsername(username: String): String

    @Query("SELECT * FROM users WHERE users.id IN (:ids)")
    suspend fun getUsersWithListOfIds(ids: List<String>): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: List<Activity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestions(users: List<UserSuggestion>)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteWithId(userId: String)

    @Update
    suspend fun update(user: UserSuggestion)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM user_suggestion WHERE accountId = :accountId")
    suspend fun clearSuggestions(accountId: String = Firebase.auth.currentUser?.uid!!)

    @Query("DELETE FROM activity WHERE accountId = :accountId")
    suspend fun clearActivity(accountId: String = Firebase.auth.currentUser?.uid!!)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}