package com.salazar.cheers.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.salazar.cheers.data.db.entities.*
import com.salazar.cheers.domain.models.ChatChannel
import com.salazar.cheers.domain.models.ChatMessage
import com.salazar.cheers.internal.*


@TypeConverters(Converters::class)
@Database(
    entities = [
        RecentUser::class,
        Post::class,
        Party::class,
        UserItem::class,
        User::class,
        UserPreference::class,
        RemoteKey::class,
        StoryRemoteKey::class,
        EventRemoteKey::class,
        Story::class,
        ChatChannel::class,
        ChatMessage::class,
        UserStats::class,
        Activity::class,
        UserSuggestion::class,
        Ticket::class,
        Comment::class,
    ],
    version = 12,
    exportSchema = false,
    autoMigrations = []
)
abstract class CheersDatabase : RoomDatabase() {
    abstract fun cheersDao(): CheersDao
    abstract fun postDao(): PostDao
    abstract fun partyDao(): PartyDao
    abstract fun storyDao(): StoryDao
    abstract fun userDao(): UserDao
    abstract fun userItemDao(): UserItemDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun chatDao(): ChatDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun activityDao(): ActivityDao
    abstract fun ticketDao(): TicketDao
    abstract fun commentDao(): CommentDao
}