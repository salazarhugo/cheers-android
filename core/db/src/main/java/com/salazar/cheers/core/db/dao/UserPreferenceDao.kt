package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Query("SELECT * FROM userPreference WHERE id = :userId")
    fun getUserPreference(userId: String): Flow<UserPreferenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userPreference: UserPreferenceEntity)

    @Delete
    suspend fun delete(userPreference: UserPreferenceEntity)

    @Update
    suspend fun update(userPreference: UserPreferenceEntity)

    @Query("DELETE FROM userPreference")
    suspend fun clearAll()
}