package com.salazar.cheers.di

import android.content.Context
import androidx.datastore.core.DataStore
import cheers.activity.v1.ActivityServiceGrpcKt
import cheers.party.v1.PartyServiceGrpcKt
import cheers.post.v1.PostServiceGrpcKt
import cheers.user.v1.UserServiceGrpcKt
import cheers.chat.v1.ChatServiceGrpcKt
import cheers.notification.v1.NotificationServiceGrpcKt
import cheers.story.v1.StoryServiceGrpcKt
import com.salazar.cheers.Settings
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.remote.ErrorHandleInterceptor
import com.salazar.cheers.data.repository.activity.ActivityRepository
import com.salazar.cheers.data.repository.activity.impl.ActivityRepositoryImpl
import com.salazar.cheers.data.repository.party.PartyRepository
import com.salazar.cheers.data.repository.party.impl.PartyRepositoryImpl
import com.salazar.cheers.data.repository.story.StoryRepository
import com.salazar.cheers.data.repository.story.impl.StoryRepositoryImpl
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
    fun providePartyRepository(
        partyRepositoryImpl: PartyRepositoryImpl,
    ): PartyRepository {
        return partyRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideActivityRepository(
        activityRepositoryImpl: ActivityRepositoryImpl,
    ): ActivityRepository {
        return activityRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl,
    ): StoryRepository {
        return storyRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideStoryServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): StoryServiceGrpcKt.StoryServiceCoroutineStub {
        return StoryServiceGrpcKt
            .StoryServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideActivityServiceCoroutineStub(
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): ActivityServiceGrpcKt.ActivityServiceCoroutineStub {
        val a =  ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
        return ActivityServiceGrpcKt
            .ActivityServiceCoroutineStub(a)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideChatServiceCoroutineStub(
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): ChatServiceGrpcKt.ChatServiceCoroutineStub {
        val a =  ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
        return ChatServiceGrpcKt
            .ChatServiceCoroutineStub(a)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideNotificationServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): NotificationServiceGrpcKt.NotificationServiceCoroutineStub {
        return NotificationServiceGrpcKt
            .NotificationServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
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
    fun providePartyDao(
        cheersDatabase: CheersDatabase,
    ): PartyDao {
        return cheersDatabase.partyDao()
    }

    @Singleton
    @Provides
    fun provideActivityDao(
        cheersDatabase: CheersDatabase,
    ): ActivityDao {
        return cheersDatabase.activityDao()
    }

    @Singleton
    @Provides
    fun provideUserStatsDao(
        cheersDatabase: CheersDatabase,
    ): UserStatsDao {
        return cheersDatabase.userStatsDao()
    }

    @Singleton
    @Provides
    fun provideStoryDao(
        cheersDatabase: CheersDatabase,
    ): StoryDao {
        return cheersDatabase.storyDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(
        cheersDatabase: CheersDatabase,
    ): UserDao {
        return cheersDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideUserItemDao(
        cheersDatabase: CheersDatabase,
    ): UserItemDao {
        return cheersDatabase.userItemDao()
    }

    @Singleton
    @Provides
    fun provideChatDao(
        cheersDatabase: CheersDatabase,
    ): ChatDao {
        return cheersDatabase.chatDao()
    }

    @Singleton
    @Provides
    fun provideUserPreferenceDao(
        cheersDatabase: CheersDatabase,
    ): UserPreferenceDao {
        return cheersDatabase.userPreferenceDao()
    }

    @Singleton
    @Provides
    fun providePostDao(
        cheersDatabase: CheersDatabase,
    ): PostDao {
        return cheersDatabase.postDao()
    }

    @Singleton
    @Provides
    fun provideCheersDao(
        cheersDatabase: CheersDatabase,
    ): CheersDao {
        return cheersDatabase.cheersDao()
    }
}