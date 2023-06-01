package com.salazar.cheers.data.db

import androidx.room.*
import com.salazar.cheers.data.user.UserPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Query("SELECT * FROM userPreference WHERE id = :userId")
    fun getUserPreference(userId: String): Flow<UserPreference>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPreference: UserPreference)

    @Delete
    suspend fun delete(userPreference: UserPreference)

    @Update
    suspend fun update(userPreference: UserPreference)

    @Query("DELETE FROM userPreference")
    suspend fun clearAll()
}