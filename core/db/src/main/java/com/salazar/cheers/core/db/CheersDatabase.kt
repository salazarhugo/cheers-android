package com.salazar.cheers.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.salazar.cheers.core.db.dao.ActivityDao
import com.salazar.cheers.core.db.dao.ChatDao
import com.salazar.cheers.core.db.dao.CheersDao
import com.salazar.cheers.core.db.dao.CommentDao
import com.salazar.cheers.core.db.dao.DrinkDao
import com.salazar.cheers.core.db.dao.FriendRequestDao
import com.salazar.cheers.core.db.dao.NoteDao
import com.salazar.cheers.core.db.dao.PartyDao
import com.salazar.cheers.core.db.dao.PostDao
import com.salazar.cheers.core.db.dao.StoryDao
import com.salazar.cheers.core.db.dao.TicketDao
import com.salazar.cheers.core.db.dao.UserDao
import com.salazar.cheers.core.db.dao.UserItemDao
import com.salazar.cheers.core.db.dao.UserPreferenceDao
import com.salazar.cheers.core.db.dao.UserStatsDao
import com.salazar.cheers.core.db.model.ActivityEntity
import com.salazar.cheers.core.db.model.ChatChannelEntity
import com.salazar.cheers.core.db.model.ChatMessageEntity
import com.salazar.cheers.core.db.model.CommentEntity
import com.salazar.cheers.core.db.model.DrinkEntity
import com.salazar.cheers.core.db.model.EventRemoteKey
import com.salazar.cheers.core.db.model.FriendRequestEntity
import com.salazar.cheers.core.db.model.NoteEntity
import com.salazar.cheers.core.db.model.PartyEntity
import com.salazar.cheers.core.db.model.PostEntity
import com.salazar.cheers.core.db.model.RecentSearchEntity
import com.salazar.cheers.core.db.model.RemoteKey
import com.salazar.cheers.core.db.model.Story
import com.salazar.cheers.core.db.model.StoryRemoteKey
import com.salazar.cheers.core.db.model.TicketEntity
import com.salazar.cheers.core.db.model.UserEntity
import com.salazar.cheers.core.db.model.UserItemEntity
import com.salazar.cheers.core.db.model.UserPreferenceEntity
import com.salazar.cheers.core.db.model.UserStatsEntity
import com.salazar.cheers.core.db.model.UserSuggestionEntity


@TypeConverters(Converters::class)
@Database(
    entities = [
        RecentSearchEntity::class,
        PostEntity::class,
        PartyEntity::class,
        UserItemEntity::class,
        UserEntity::class,
        UserPreferenceEntity::class,
        RemoteKey::class,
        StoryRemoteKey::class,
        EventRemoteKey::class,
        Story::class,
        ChatChannelEntity::class,
        ChatMessageEntity::class,
        UserStatsEntity::class,
        ActivityEntity::class,
        UserSuggestionEntity::class,
        TicketEntity::class,
        CommentEntity::class,
        FriendRequestEntity::class,
        NoteEntity::class,
        DrinkEntity::class,
    ],
    version = 43,
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
    abstract fun drinkDao(): DrinkDao
}