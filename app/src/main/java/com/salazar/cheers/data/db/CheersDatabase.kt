package com.salazar.cheers.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.protobuf.Timestamp
import com.salazar.cheers.data.entities.*
import com.salazar.cheers.internal.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@TypeConverters(Converters::class)
@Database(
    entities = [
        RecentUser::class,
        Post::class,
        Event::class,
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
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 3, to = 4),
        AutoMigration (from = 4, to = 5),
    ]
)
abstract class CheersDatabase : RoomDatabase() {
    abstract fun cheersDao(): CheersDao
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun storyDao(): StoryDao
    abstract fun userDao(): UserDao
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

        val MIGRATION_2_3 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `user_suggestion` (`id` STRING, `name` STRING, `username` STRING, `avatar` STRING, `verified` BOOLEAN, `followBack` BOOLEAN, PRIMARY KEY(`id`))")
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CheersDatabase::class.java, "cheers.db"
            )
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
    }
}

class Converters {
    @TypeConverter
    fun fromActivityType(value: ActivityType) = value.name

    @TypeConverter
    fun toActivityType(name: String) =
        ActivityType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: ActivityType.NONE

    @TypeConverter
    fun fromMessageType(value: com.salazar.cheers.MessageType) = value.name

    @TypeConverter
    fun toMessageType(name: String) =
        com.salazar.cheers.MessageType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: com.salazar.cheers.MessageType.UNRECOGNIZED

    @TypeConverter
    fun fromRoomType(value: com.salazar.cheers.RoomType) = value.name

    @TypeConverter
    fun toRoomType(name: String) =
        com.salazar.cheers.RoomType.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: com.salazar.cheers.RoomType.UNRECOGNIZED

    @TypeConverter
    fun fromRoomStatus(value: com.salazar.cheers.RoomStatus) = value.name

    @TypeConverter
    fun toRoomStatus(name: String) =
        com.salazar.cheers.RoomStatus.values()
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: com.salazar.cheers.RoomStatus.UNRECOGNIZED

    @TypeConverter
    fun fromBeverage(value: Beverage) = value.name

    @TypeConverter
    fun toBeverage(name: String) = Beverage.fromName(name)

    @TypeConverter
    fun fromTimestamp(value: Timestamp) = value.seconds

    @TypeConverter
    fun toTimestamp(value: Long): Timestamp = Timestamp.newBuilder().setSeconds(value).build()

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