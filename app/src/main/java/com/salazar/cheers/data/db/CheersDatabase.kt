package com.salazar.cheers.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.salazar.cheers.data.db.entities.*
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
    ],
    version = 1,
    exportSchema = true,
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
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun storyRemoteKeyDao(): StoryRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao

    companion object {
        @Volatile
        private var instance: CheersDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CheersDatabase::class.java, "cheers.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}