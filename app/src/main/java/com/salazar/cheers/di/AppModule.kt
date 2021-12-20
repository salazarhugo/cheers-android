package com.salazar.cheers.di

import android.content.Context
import androidx.room.Room
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.db.CheersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): CheersDatabase {
        return Room
            .databaseBuilder(context.applicationContext, CheersDatabase::class.java, "forex.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCheersDao(@ApplicationContext appContext: Context): CheersDao {
        return CheersDatabase.invoke(appContext).forexDao()
    }
}