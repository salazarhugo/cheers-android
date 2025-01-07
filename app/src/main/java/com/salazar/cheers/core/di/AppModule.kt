package com.salazar.cheers.core.di

import android.content.Context
import android.location.Geocoder
import androidx.work.WorkManager
import cheers.account.v1.AccountServiceGrpcKt
import cheers.activity.v1.ActivityServiceGrpcKt
import cheers.auth.v1.AuthServiceGrpcKt
import cheers.chat.v1.ChatServiceGrpcKt
import cheers.comment.v1.CommentServiceGrpcKt
import cheers.drink.v1.DrinkServiceGrpcKt
import cheers.friendship.v1.FriendshipServiceGrpcKt
import cheers.location.v1.LocationServiceGrpcKt
import cheers.media.v1.MediaServiceGrpcKt
import cheers.note.v1.NoteServiceGrpcKt
import cheers.notification.v1.NotificationServiceGrpcKt
import cheers.party.v1.PartyServiceGrpcKt
import cheers.post.v1.PostServiceGrpcKt
import cheers.search.v1.SearchServiceGrpcKt
import cheers.story.v1.StoryServiceGrpcKt
import cheers.ticket.v1.TicketServiceGrpcKt
import cheers.user.v1.UserServiceGrpcKt
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.salazar.cheers.BuildConfig
import com.salazar.cheers.core.data.remote.FirebaseUserIdTokenInterceptor
import com.salazar.cheers.core.data.remote.LoggerInterceptor
import com.salazar.cheers.core.data.remote.TokenInterceptor
import com.salazar.cheers.core.db.CheersDatabase
import com.salazar.cheers.core.db.dao.CheersDao
import com.salazar.cheers.core.db.dao.StoryDao
import com.salazar.cheers.core.db.dao.UserPreferenceDao
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.cheers.data.activity.impl.ActivityRepositoryImpl
import com.salazar.cheers.data.billing.api.ApiService
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.chat.repository.ChatRepositoryImpl
import com.salazar.cheers.data.comment.CommentRepository
import com.salazar.cheers.data.comment.CommentRepositoryImpl
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.data.note.repository.NoteRepositoryImpl
import com.salazar.cheers.data.repository.story.StoryRepository
import com.salazar.cheers.data.repository.story.impl.StoryRepositoryImpl
import com.salazar.cheers.data.ticket.TicketRepository
import com.salazar.cheers.data.ticket.impl.TicketRepositoryImpl
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.data.user.account.AccountRepository
import com.salazar.cheers.data.user.account.AccountRepositoryImpl
import com.salazar.cheers.domain.get_id_token.GetIdTokenUseCase
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGeocoder(
        @ApplicationContext context: Context,
    ): Geocoder {
        return Geocoder(context)
    }

    @Singleton
    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context,
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideFirebaseUserIdTokenInterceptor(
        getIdTokenUseCase: GetIdTokenUseCase,
    ): FirebaseUserIdTokenInterceptor {
        return FirebaseUserIdTokenInterceptor(getIdTokenUseCase)
    }

    @Singleton
    @Provides
    fun provideApiService(
        authTokenInterceptor: FirebaseUserIdTokenInterceptor,
    ): ApiService {
        val moshi = Moshi.Builder().build()

        val client = OkHttpClient.Builder().build()

        val okHttpClient: OkHttpClient = client.newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(authTokenInterceptor)
            .build()

        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://europe-west2-cheers-a275e.cloudfunctions.net")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }


    @Provides
    fun provideWebSocket(
        chatWebSocketListener: com.salazar.cheers.data.chat.websocket.ChatWebSocketListener,
    ): WebSocket {
        val authToken = "55FEvHawinQCa9jgH7ZdWESR3ri2"
        val request = Request.Builder()
            .url("${Constants.WEBSOCKET_URL}?token=" + authToken)
//            .addHeader("Sec-Websocket-Protocol", )
            .build()
        val client = OkHttpClient()
        return client.newWebSocket(request, chatWebSocketListener)
    }

    @Provides
    @Singleton
    fun provideManagedChannel(): ManagedChannel {
        return ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl,
    ): ChatRepository {
        return chatRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl,
    ): NoteRepository {
        return noteRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideTicketRepository(
        ticketRepositoryImpl: TicketRepositoryImpl,
    ): TicketRepository {
        return ticketRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideFriendshipRepository(
        commentRepositoryImpl: com.salazar.cheers.data.friendship.FriendshipRepositoryImpl,
    ): com.salazar.cheers.data.friendship.FriendshipRepository {
        return commentRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userRepositoryImpl: com.salazar.cheers.data.user.UserRepositoryImpl,
    ): UserRepository {
        return userRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl,
    ): AccountRepository {
        return accountRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl,
    ): CommentRepository {
        return commentRepositoryImpl
    }

    @Provides
    @Singleton
    fun providePartyRepository(
        partyRepositoryImpl: com.salazar.cheers.data.party.data.repository.impl.PartyRepositoryImpl,
    ): com.salazar.cheers.data.party.data.repository.PartyRepository {
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
    fun provideSearchServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): SearchServiceGrpcKt.SearchServiceCoroutineStub {
        return SearchServiceGrpcKt
            .SearchServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor, loggerInterceptor)
    }

    @Provides
    @Singleton
    fun provideAuthServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): AuthServiceGrpcKt.AuthServiceCoroutineStub {
        return AuthServiceGrpcKt
            .AuthServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
    }

    @Provides
    @Singleton
    fun provideMediaServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): MediaServiceGrpcKt.MediaServiceCoroutineStub {
        return MediaServiceGrpcKt
            .MediaServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
    }

    @Provides
    @Singleton
    fun provideNoteServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): NoteServiceGrpcKt.NoteServiceCoroutineStub {
        return NoteServiceGrpcKt
            .NoteServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
    }

    @Provides
    @Singleton
    fun provideDrinkServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): DrinkServiceGrpcKt.DrinkServiceCoroutineStub {
        return DrinkServiceGrpcKt
            .DrinkServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
    }

    @Provides
    @Singleton
    fun provideLocationServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): LocationServiceGrpcKt.LocationServiceCoroutineStub {
        return LocationServiceGrpcKt
            .LocationServiceCoroutineStub(managedChannel)
            .withInterceptors(loggerInterceptor)
            .withInterceptors(tokenInterceptor)
    }

    @Provides
    @Singleton
    fun provideAccountServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): AccountServiceGrpcKt.AccountServiceCoroutineStub {
        return AccountServiceGrpcKt
            .AccountServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideFriendshipServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): FriendshipServiceGrpcKt.FriendshipServiceCoroutineStub {
        return FriendshipServiceGrpcKt
            .FriendshipServiceCoroutineStub(managedChannel)
            .withInterceptors(loggerInterceptor)
            .withInterceptors(tokenInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideCommentServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): CommentServiceGrpcKt.CommentServiceCoroutineStub {
        return CommentServiceGrpcKt
            .CommentServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideStoryServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
    ): StoryServiceGrpcKt.StoryServiceCoroutineStub {
        return StoryServiceGrpcKt
            .StoryServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideTicketServiceCoroutineStub(
        tokenInterceptor: TokenInterceptor,
    ): TicketServiceGrpcKt.TicketServiceCoroutineStub {
        val a = ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
        return TicketServiceGrpcKt
            .TicketServiceCoroutineStub(a)
            .withInterceptors(tokenInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideActivityServiceCoroutineStub(
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): ActivityServiceGrpcKt.ActivityServiceCoroutineStub {
        val a = ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
        return ActivityServiceGrpcKt
            .ActivityServiceCoroutineStub(a)
            .withInterceptors(tokenInterceptor)
            .apply {
                if (BuildConfig.DEBUG)
                    return withInterceptors(loggerInterceptor)
            }
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideChatServiceCoroutineStub(
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): ChatServiceGrpcKt.ChatServiceCoroutineStub {
        val a = ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
        return ChatServiceGrpcKt
            .ChatServiceCoroutineStub(a)
            .withInterceptors(tokenInterceptor)
            .apply {
                if (BuildConfig.DEBUG)
                    return withInterceptors(loggerInterceptor)
            }
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideNotificationServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): NotificationServiceGrpcKt.NotificationServiceCoroutineStub {
        return NotificationServiceGrpcKt
            .NotificationServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
            .withInterceptors(loggerInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideUserServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): UserServiceGrpcKt.UserServiceCoroutineStub {
        return UserServiceGrpcKt
            .UserServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor, loggerInterceptor)
    }

    @Provides
    @Singleton
    fun providePartyServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): PartyServiceGrpcKt.PartyServiceCoroutineStub {
        return PartyServiceGrpcKt
            .PartyServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
            .apply {
                if (BuildConfig.DEBUG)
                    return withInterceptors(loggerInterceptor)
            }
    }

    @Provides
    @Singleton
    fun providePostServiceCoroutineStub(
        managedChannel: ManagedChannel,
        tokenInterceptor: TokenInterceptor,
        loggerInterceptor: LoggerInterceptor,
    ): PostServiceGrpcKt.PostServiceCoroutineStub {
        return PostServiceGrpcKt
            .PostServiceCoroutineStub(managedChannel)
            .withInterceptors(tokenInterceptor)
            .apply {
                if (BuildConfig.DEBUG)
                    return withInterceptors(loggerInterceptor)
            }
            .withInterceptors()
    }

    @Singleton
    @Provides
    fun provideAppUpdateManager(@ApplicationContext applicationContext: Context): AppUpdateManager {
        return AppUpdateManagerFactory.create(applicationContext)
    }

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): CheersDatabase {
        return androidx.room.Room
            .databaseBuilder(
                context.applicationContext,
                CheersDatabase::class.java,
                "cheers.db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideNoteDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.NoteDao {
        return cheersDatabase.noteDao()
    }

    @Singleton
    @Provides
    fun provideDrinkDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.DrinkDao {
        return cheersDatabase.drinkDao()
    }

    @Singleton
    @Provides
    fun provideFriendRequestDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.FriendRequestDao {
        return cheersDatabase.friendRequestDao()
    }

    @Singleton
    @Provides
    fun provideCommentDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.CommentDao {
        return cheersDatabase.commentDao()
    }

    @Singleton
    @Provides
    fun provideTicketDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.TicketDao {
        return cheersDatabase.ticketDao()
    }

    @Singleton
    @Provides
    fun providePartyDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.PartyDao {
        return cheersDatabase.partyDao()
    }

    @Singleton
    @Provides
    fun provideActivityDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.ActivityDao {
        return cheersDatabase.activityDao()
    }

    @Singleton
    @Provides
    fun provideUserStatsDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.UserStatsDao {
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
    ): com.salazar.cheers.core.db.dao.UserDao {
        return cheersDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideUserItemDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.UserItemDao {
        return cheersDatabase.userItemDao()
    }

    @Singleton
    @Provides
    fun provideChatDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.core.db.dao.ChatDao {
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
    ): com.salazar.cheers.core.db.dao.PostDao {
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