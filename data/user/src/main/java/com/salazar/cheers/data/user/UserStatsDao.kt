package com.salazar.cheers.data.user

import androidx.room.*

@Dao
interface UserStatsDao {

    @Query("SELECT * FROM user_stats WHERE username = :username")
    suspend fun getUserStats(username: String): UserStats

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userStats: UserStats)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usersStats: List<UserStats>)

    @Delete
    suspend fun delete(userStats: UserStats)

    @Query("DELETE FROM user_stats")
    suspend fun clearAll()
}