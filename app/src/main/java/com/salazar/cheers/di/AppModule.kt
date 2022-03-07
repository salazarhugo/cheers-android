package com.salazar.cheers.di

import android.content.Context
import androidx.room.Room
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.db.*
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
            .databaseBuilder(context.applicationContext, CheersDatabase::class.java, "cheers.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideStoryDao(@ApplicationContext appContext: Context): StoryDao {
        return CheersDatabase.invoke(appContext).storyDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(@ApplicationContext appContext: Context): UserDao {
        return CheersDatabase.invoke(appContext).userDao()
    }

    @Singleton
    @Provides
    fun provideUserPreferenceDao(@ApplicationContext appContext: Context): UserPreferenceDao {
        return CheersDatabase.invoke(appContext).userPreferenceDao()
    }

    @Singleton
    @Provides
    fun providePostDao(@ApplicationContext appContext: Context): PostDao {
        return CheersDatabase.invoke(appContext).postDao()
    }

    @Singleton
    @Provides
    fun provideCheersDao(@ApplicationContext appContext: Context): CheersDao {
        return CheersDatabase.invoke(appContext).cheersDao()
    }

    @Singleton
    @Provides
    fun provideNeo4jService(): Neo4jService {
        return Neo4jService()
    }
}