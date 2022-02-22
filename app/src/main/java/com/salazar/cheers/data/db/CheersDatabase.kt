package com.salazar.cheers.data.db

import android.content.Context
import androidx.room.*
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.data.entities.RemoteKey
import com.salazar.cheers.data.entities.UserPreference
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@TypeConverters(Converters::class)
@Database(
    entities = [RecentUser::class, Post::class, User::class, RemoteKey::class, UserPreference::class],
    version = 1,
    exportSchema = false
)
abstract class CheersDatabase : RoomDatabase() {

    abstract fun cheersDao(): CheersDao
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun remoteKeyDao(): RemoteKeyDao

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
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}