package com.salazar.cheers.data.db

import androidx.room.*
import com.salazar.cheers.data.user.RecentUser
import kotlinx.coroutines.flow.Flow

@Dao
interface CheersDao {
    @Transaction
    @Query("SELECT * FROM recentUsers ORDER BY date DESC")
    fun getRecentUsers(): Flow<List<RecentUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentUser(user: RecentUser): Long

    @Delete
    suspend fun deleteRecentUser(user: RecentUser)
}