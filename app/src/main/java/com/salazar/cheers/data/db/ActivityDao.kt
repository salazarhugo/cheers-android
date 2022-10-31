package com.salazar.cheers.data.db

import androidx.room.*
import cheers.chat.v1.RoomStatus
import cheers.chat.v1.RoomType
import com.google.firebase.auth.FirebaseAuth
import com.google.protobuf.Timestamp
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import kotlinx.coroutines.flow.Flow
import cheers.chat.v1.MessageType
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.Post

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<Activity>)

    @Query("SELECT * FROM activity WHERE accountId = :accountId ORDER BY activity.time DESC")
    suspend fun listActivity(accountId: String): List<Activity>
}