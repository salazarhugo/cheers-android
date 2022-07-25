package com.salazar.cheers.data.db

import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.RoomStatus
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatMessage
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: ChatChannel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM room WHERE id = :channelId")
    suspend fun deleteChannel(channelId: String)

    @Query("DELETE FROM room")
    suspend fun deleteChannels()

    @Transaction
    @Query("SELECT * FROM message WHERE chatChannelId = :channelId ORDER BY time DESC")
    fun getMessages(channelId: String): Flow<List<ChatMessage>>

    @Transaction
    @Query("SELECT * FROM room WHERE accountId = :me ORDER BY recentMessageTime DESC")
    fun getChannels(me: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<ChatChannel>>

    @Query("SELECT COUNT(id) FROM room WHERE status = :status AND accountId = :accountId")
    fun getUnreadChatCount(
        status: RoomStatus = RoomStatus.NEW,
        accountId: String = FirebaseAuth.getInstance().currentUser?.uid!!
    ): Flow<Int>

    @Transaction
    @Query("SELECT * FROM room WHERE id = :channelId")
    suspend fun getChannel(channelId: String): ChatChannel

    @Transaction
    @Query("SELECT * FROM room WHERE id = :channelId")
    fun getChannelFlow(channelId: String): Flow<ChatChannel>

    @Query("UPDATE room SET status = :status WHERE id = :channelId")
    suspend fun setStatus(
        channelId: String,
        status: RoomStatus
    )

    suspend fun seenChannel(channelId: String) {
        val channel = getChannel(channelId = channelId)
        try {
            if (channel.status == RoomStatus.NEW)
                setStatus(channelId, RoomStatus.RECEIVED)
        } catch (e: Exception) {

        }
    }
}

//data class DirectChannel(
//    @Embedded
//    val channel: ChatChannel,
//
//    @Relation(parentColumn = "recentMessageId", entityColumn = "id")
//    val recentMessage: TextMessage? = null,
//
//    @Relation(
//        parentColumn = "members",
//        entityColumn = "id",
//    )
//    val members: List<User> = ArrayList()
//)
