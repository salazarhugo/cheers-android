package com.salazar.cheers.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.salazar.cheers.comment.data.db.CommentDao
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.activity.Activity
import com.salazar.cheers.data.chat.db.ChatDao
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.db.entities.*
import com.salazar.cheers.data.drink.db.DrinkDao
import com.salazar.cheers.data.friendship.FriendRequest
import com.salazar.cheers.data.note.Note
import com.salazar.cheers.data.note.db.NoteDao
import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.party.PartyDao
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.post.repository.PostDao
import com.salazar.cheers.data.ticket.TicketDao
import com.salazar.cheers.data.user.RecentUser
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserDao
import com.salazar.cheers.data.user.UserItemDao
import com.salazar.cheers.data.user.UserPreference
import com.salazar.cheers.data.user.UserStats
import com.salazar.cheers.data.user.UserStatsDao
import com.salazar.cheers.data.user.UserSuggestion
import com.salazar.cheers.data.chat.models.ChatChannel
import com.salazar.cheers.data.chat.models.ChatMessage


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
        FriendRequest::class,
        Note::class,
        Drink::class,
    ],
    version = 37,
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
    abstract fun activityDao(): com.salazar.cheers.data.activity.ActivityDao
    abstract fun ticketDao(): TicketDao
    abstract fun commentDao(): CommentDao
    abstract fun friendRequestDao(): com.salazar.cheers.data.friendship.FriendRequestDao
    abstract fun noteDao(): NoteDao
    abstract fun drinkDao(): DrinkDao
}