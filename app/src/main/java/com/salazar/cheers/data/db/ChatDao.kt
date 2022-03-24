package com.salazar.cheers.data.db

import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.User

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: ChatChannel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChatChannel>)

    @Transaction
    @Query("SELECT * FROM channel WHERE members LIKE '%' || :memberId || '%'")
    suspend fun getChannels(memberId: String = FirebaseAuth.getInstance().currentUser?.uid!!): List<DirectChannel>

    @Transaction
    @Query("SELECT * FROM channel WHERE id = :channelId")
    suspend fun getChannel(channelId: String): DirectChannel

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}

data class DirectChannel(
    @Embedded
    val channel: ChatChannel,

//    @Relation(parentColumn = "otherUserId", entityColumn = "id")
//    val otherUser: User,
//
//    @Relation(parentColumn = "recentMessageId", entityColumn = "id")
//    val recentMessage: TextMessage = TextMessage(),

    @Relation(
        parentColumn = "members",
        entityColumn = "id",
    )
    val members: List<User> = ArrayList()
)
