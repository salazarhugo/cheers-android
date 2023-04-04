package com.salazar.cheers.data.db

import androidx.room.*
import com.salazar.cheers.core.data.internal.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<Activity>)

    @Query("SELECT * FROM activity WHERE accountId = :accountId ORDER BY activity.createTime DESC")
    suspend fun listActivity(accountId: String): List<Activity>

    @Query("SELECT COUNT(*) FROM activity WHERE accountId = :accountId AND acknowledged = 0")
    fun countUnreadActivity(accountId: String): Flow<Int>

    @Query("UPDATE activity SET acknowledged = 1")
    suspend fun acknowledgeAll()

    @Query("DELETE FROM activity")
    suspend fun clear()

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<Activity>) {
        clear()
        insertAll(activities = activities)
    }
}