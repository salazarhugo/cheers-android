package com.salazar.cheers.data.db

import androidx.room.*
import com.salazar.cheers.internal.Activity

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<Activity>)

    @Query("SELECT * FROM activity WHERE accountId = :accountId ORDER BY activity.createTime DESC")
    suspend fun listActivity(accountId: String): List<Activity>

    @Query("DELETE FROM activity")
    suspend fun clear()

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<Activity>) {
        clear()
        insertAll(activities = activities)
    }
}