package com.salazar.cheers.data.db

import android.content.Context
import androidx.room.*
import com.google.firebase.Timestamp
import com.salazar.cheers.data.entities.*
import com.salazar.cheers.internal.Beverage
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@TypeConverters(Converters::class)
@Database(
    entities = [
        RecentUser::class,
        Post::class,
        User::class,
        RemoteKey::class,
        UserPreference::class,
        StoryRemoteKey::class,
        StoryResponse::class,
//        ChatChannel::class,
//        TextMessage::class,
        UserStats::class,
    ],
    version = 1,
    exportSchema = false,
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2)
//    ]
)
abstract class CheersDatabase : RoomDatabase() {

    abstract fun cheersDao(): CheersDao
    abstract fun postDao(): PostDao
    abstract fun storyDao(): StoryDao
    abstract fun userDao(): UserDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun chatDao(): ChatDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun storyRemoteKeyDao(): StoryRemoteKeyDao

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

class Converters {
    @TypeConverter
    fun fromBeverage(value: Beverage) = value.name

    @TypeConverter
    fun toBeverage(name: String) = Beverage.fromName(name)

    @TypeConverter
    fun fromTimestamp(value: Timestamp?) = value?.seconds ?: Timestamp.now().seconds

    @TypeConverter
    fun toTimestamp(value: Long) = Timestamp(value, 0)

    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}