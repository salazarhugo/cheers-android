package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.UserStatsEntity

@Dao
interface UserStatsDao {

    @Query("SELECT * FROM user_stats WHERE username = :username")
    suspend fun getUserStats(username: String): UserStatsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userStats: UserStatsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usersStats: List<UserStatsEntity>)

    @Delete
    suspend fun delete(userStats: UserStatsEntity)

    @Query("DELETE FROM user_stats")
    suspend fun clearAll()
}