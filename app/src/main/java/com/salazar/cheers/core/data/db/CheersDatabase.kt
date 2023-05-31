package com.salazar.cheers.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.salazar.cheers.comment.data.db.CommentDao
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.core.data.internal.*
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.db.entities.*
import com.salazar.cheers.feature.chat.data.db.ChatDao
import com.salazar.cheers.feature.chat.domain.models.ChatChannel
import com.salazar.cheers.feature.chat.domain.models.ChatMessage
import com.salazar.cheers.friendship.domain.models.FriendRequest
import com.salazar.cheers.notes.data.db.NoteDao
import com.salazar.cheers.notes.domain.models.Note


@TypeConverters(Converters::class)
@Database(
    entities = [
        RecentUser::class,
        Post::class,
        Party::class,
        com.salazar.cheers.core.model.UserItem::class,
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
        FriendRequest::class,
        Note::class,
    ],
    version = 26,
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
    abstract fun friendRequestDao(): FriendRequestDao
    abstract fun noteDao(): NoteDao
}