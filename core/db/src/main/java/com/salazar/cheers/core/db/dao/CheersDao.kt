package com.salazar.cheers.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.salazar.cheers.core.db.model.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheersDao {
    @Transaction
    @Query("SELECT * FROM recent_searches ORDER BY date DESC")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Query("SELECT * FROM recent_searches WHERE id = :userID")
    fun getRecentUser(userID: String): RecentSearchEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentUser(search: RecentSearchEntity): Long

    @Delete
    suspend fun deleteRecentUser(search: RecentSearchEntity)
}