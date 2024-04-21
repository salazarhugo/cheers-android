package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.RecentUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheersDao {
    @Transaction
    @Query("SELECT * FROM recentUsers ORDER BY date DESC")
    fun getRecentUsers(): Flow<List<RecentUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentUser(user: RecentUserEntity): Long

    @Delete
    suspend fun deleteRecentUser(user: RecentUserEntity)
}