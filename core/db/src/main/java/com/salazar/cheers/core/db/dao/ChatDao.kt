package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.ChatChannelEntity
import com.salazar.cheers.core.db.model.ChatMessageEntity
import com.salazar.cheers.core.model.MessageType
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.ChatType
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("DELETE FROM room")
    suspend fun clearRooms()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: ChatChannelEntity)

    @Transaction
    suspend fun insertInbox(rooms: List<ChatChannelEntity>) {
        clearRooms()
        insertRooms(rooms)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(channel: List<ChatChannelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM room WHERE id = :channelId")
    suspend fun deleteChannel(channelId: String)

    @Query("DELETE FROM message WHERE roomId = :channelId")
    suspend fun deleteChannelMessages(channelId: String)

    @Query("DELETE FROM room")
    suspend fun deleteChannels()

    @Transaction
    suspend fun incrementUnreadCount(chatID: String) {
        val channel = getChannel(chatID)
        updateUnreadCount(chatID, channel.unreadCount + 1)
    }

    @Transaction
    @Query("UPDATE room SET unreadCount = :count WHERE id = :channelId")
    fun updateUnreadCount(channelId: String, count: Int)

    @Transaction
    @Query("UPDATE room SET lastMessage = :message, lastMessageTime = :time, lastMessageType = :type WHERE id = :channelId")
    fun updateLastMessage(channelId: String, message: String, time: Long, type: MessageType)

    @Query("UPDATE room SET pinned = :pinned WHERE id = :roomId")
    fun updatePinnedRoom(roomId: String, pinned: Boolean)

    @Transaction
    @Query("SELECT * FROM message WHERE roomId = :channelId ORDER BY createTime DESC")
    fun getMessages(channelId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM room ORDER BY pinned DESC, lastMessageTime DESC")
    fun getChannels(): Flow<List<ChatChannelEntity>>

    @Query("SELECT COUNT(id) FROM room WHERE status = :status")
    fun getUnreadChatCount(
        status: ChatStatus = ChatStatus.NEW,
    ): Flow<Int>

    @Transaction
    @Query("SELECT * FROM room WHERE id = :channelId")
    suspend fun getChannel(channelId: String): ChatChannelEntity

    @Transaction
    @Query("SELECT * FROM room WHERE id = :channelId")
    fun getChannelFlow(channelId: String): Flow<ChatChannelEntity?>

    @Transaction
    @Query("SELECT * FROM room WHERE type = :direct AND members  LIKE '%' || :userId || '%' LIMIT 1")
    fun getChatWithUser(userId: String, direct: ChatType = ChatType.DIRECT): ChatChannelEntity?

    @Query("UPDATE room SET status = :status WHERE id = :channelId")
    suspend fun setStatus(
        channelId: String,
        status: ChatStatus
    )
}
