package com.salazar.cheers.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.Post

@Database(entities = [RecentUser::class], version = 1, exportSchema = false)
abstract class CheersDatabase : RoomDatabase() {
    abstract fun forexDao(): CheersDao

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