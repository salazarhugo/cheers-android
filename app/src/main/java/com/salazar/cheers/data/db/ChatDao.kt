package com.salazar.cheers.data.db

import androidx.room.Dao


@Dao
interface ChatDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(channel: ChatChannel)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertMessages(messages: List<TextMessage>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMessage(vararg messages: TextMessage)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertChannel(channel: ChatChannel)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(channels: List<ChatChannel>)

//    @Transaction
//    @Query("SELECT * FROM message WHERE chatChannelId = :channelId ORDER BY time DESC")
//    fun getMessages(channelId: String): Flow<List<TextMessage>>

//    @Transaction
//    @Query("SELECT * FROM channel WHERE members LIKE '%' || :memberId || '%' ORDER BY recentMessageTime DESC")
//    fun getChannels(memberId: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<DirectChannel>>
//
//    @Transaction
//    @Query("SELECT * FROM channel WHERE id = :channelId")
//    suspend fun getChannel(channelId: String): DirectChannel
//
//    @Transaction
//    @Query("DELETE FROM channel WHERE id = :channelId")
//    suspend fun deleteChannel(channelId: String)
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
