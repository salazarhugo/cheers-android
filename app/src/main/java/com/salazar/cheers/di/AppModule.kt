package com.salazar.cheers.di

import android.content.Context
import androidx.datastore.core.DataStore
import cheers.party.v1.PartyServiceGrpcKt
import cheers.post.v1.PostServiceGrpcKt
import cheers.user.v1.UserServiceGrpcKt
import com.salazar.cheers.Settings
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.remote.ErrorHandleInterceptor
import com.salazar.cheers.data.serializer.settingsDataStore
import com.salazar.cheers.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideErrorHandlerInterceptor(): ErrorHandleInterceptor =
        ErrorHandleInterceptor()

    @Provides
    @Singleton
    fun provideManagedChannel(): ManagedChannel {
        return ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): UserServiceGrpcKt.UserServiceCoroutineStub {
        return UserServiceGrpcKt
            .UserServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun providePartyServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): PartyServiceGrpcKt.PartyServiceCoroutineStub {
        return PartyServiceGrpcKt
            .PartyServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun providePostServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): PostServiceGrpcKt.PostServiceCoroutineStub {
        return PostServiceGrpcKt
            .PostServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Settings> {
        return context.settingsDataStore
    }

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): CheersDatabase {
        return androidx.room.Room
            .databaseBuilder(context.applicationContext, CheersDatabase::class.java, "cheers.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserStatsDao(@ApplicationContext appContext: Context): UserStatsDao {
        return CheersDatabase.invoke(appContext).userStatsDao()
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
    fun provideUserItemDao(@ApplicationContext appContext: Context): UserItemDao {
        return CheersDatabase.invoke(appContext).userItemDao()
    }

    @Singleton
    @Provides
    fun provideChatDao(@ApplicationContext appContext: Context): ChatDao {
        return CheersDatabase.invoke(appContext).chatDao()
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
}