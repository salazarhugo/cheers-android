package com.salazar.cheers.core.di

import android.content.Context
import cheers.account.v1.AccountServiceGrpcKt
import cheers.activity.v1.ActivityServiceGrpcKt
import cheers.chat.v1.ChatServiceGrpcKt
import cheers.comment.v1.CommentServiceGrpcKt
import cheers.drink.v1.DrinkServiceGrpcKt
import cheers.friendship.v1.FriendshipServiceGrpcKt
import cheers.location.v1.LocationServiceGrpcKt
import cheers.note.v1.NoteServiceGrpcKt
import cheers.notification.v1.NotificationServiceGrpcKt
import cheers.party.v1.PartyServiceGrpcKt
import cheers.post.v1.PostServiceGrpcKt
import cheers.story.v1.StoryServiceGrpcKt
import cheers.ticket.v1.TicketServiceGrpcKt
import cheers.user.v1.UserServiceGrpcKt
import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.data.CommentRepositoryImpl
import com.salazar.cheers.comment.data.db.CommentDao
import com.salazar.cheers.core.data.api.ApiService
import com.salazar.cheers.core.data.db.CheersDatabase
import com.salazar.cheers.core.data.remote.ErrorHandleInterceptor
import com.salazar.cheers.core.data.remote.FirebaseUserIdTokenInterceptor
import com.salazar.cheers.core.data.util.Constants
import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.cheers.data.activity.impl.ActivityRepositoryImpl
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.note.db.NoteDao
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.data.note.repository.NoteRepositoryImpl
import com.salazar.cheers.data.party.PartyDao
import com.salazar.cheers.data.post.repository.PostDao
import com.salazar.cheers.data.repository.account.AccountRepository
import com.salazar.cheers.data.repository.account.AccountRepositoryImpl
import com.salazar.cheers.data.repository.story.StoryRepository
import com.salazar.cheers.data.repository.story.impl.StoryRepositoryImpl
import com.salazar.cheers.data.repository.ticket.TicketRepository
import com.salazar.cheers.data.repository.ticket.impl.TicketRepositoryImpl
import com.salazar.cheers.data.user.UserDao
import com.salazar.cheers.data.user.UserItemDao
import com.salazar.cheers.data.user.UserStatsDao
import com.salazar.cheers.drink.data.repository.DrinkRepository
import com.salazar.cheers.drink.data.repository.DrinkRepositoryImpl
import com.salazar.cheers.feature.chat.data.db.ChatDao
import com.salazar.cheers.feature.chat.data.repository.ChatRepository
import com.salazar.cheers.feature.chat.data.repository.ChatRepositoryImpl
import com.salazar.cheers.feature.chat.data.websocket.ChatWebSocketListener
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

    @Singleton
    @Provides
    fun provideFirebaseUserIdTokenInterceptor(): FirebaseUserIdTokenInterceptor {
       return FirebaseUserIdTokenInterceptor()
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
        chatWebSocketListener: ChatWebSocketListener,
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
    fun provideDrinkRepository(
        drinkRepositoryImpl: DrinkRepositoryImpl,
    ): DrinkRepository {
        return drinkRepositoryImpl
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
    fun provideNoteServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): NoteServiceGrpcKt.NoteServiceCoroutineStub {
        return NoteServiceGrpcKt
            .NoteServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
    }

    @Provides
    @Singleton
    fun provideDrinkServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): DrinkServiceGrpcKt.DrinkServiceCoroutineStub {
        return DrinkServiceGrpcKt
            .DrinkServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
    }

    @Provides
    @Singleton
    fun provideLocationServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): LocationServiceGrpcKt.LocationServiceCoroutineStub {
        return LocationServiceGrpcKt
            .LocationServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
    }

    @Provides
    @Singleton
    fun provideAccountServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): AccountServiceGrpcKt.AccountServiceCoroutineStub {
        return AccountServiceGrpcKt
            .AccountServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideFriendshipServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): FriendshipServiceGrpcKt.FriendshipServiceCoroutineStub {
        return FriendshipServiceGrpcKt
            .FriendshipServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideCommentServiceCoroutineStub(
        managedChannel: ManagedChannel,
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): CommentServiceGrpcKt.CommentServiceCoroutineStub {
        return CommentServiceGrpcKt
            .CommentServiceCoroutineStub(managedChannel)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
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
    fun provideTicketServiceCoroutineStub(
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): TicketServiceGrpcKt.TicketServiceCoroutineStub {
        val a = ManagedChannelBuilder
            .forAddress(Constants.GATEWAY_HOST, 443)
            .build()
        return TicketServiceGrpcKt
            .TicketServiceCoroutineStub(a)
            .withInterceptors(errorHandleInterceptor)
            .withInterceptors()
    }

    @Provides
    @Singleton
    fun provideActivityServiceCoroutineStub(
        errorHandleInterceptor: ErrorHandleInterceptor,
    ): ActivityServiceGrpcKt.ActivityServiceCoroutineStub {
        val a = ManagedChannelBuilder
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
        val a = ManagedChannelBuilder
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
    fun provideDb(@ApplicationContext context: Context): CheersDatabase {
        return androidx.room.Room
            .databaseBuilder(context.applicationContext, CheersDatabase::class.java, "cheers.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideNoteDao(
        cheersDatabase: CheersDatabase,
    ): NoteDao {
        return cheersDatabase.noteDao()
    }

    @Singleton
    @Provides
    fun provideFriendRequestDao(
        cheersDatabase: CheersDatabase,
    ): com.salazar.cheers.data.friendship.FriendRequestDao {
        return cheersDatabase.friendRequestDao()
    }

    @Singleton
    @Provides
    fun provideCommentDao(
        cheersDatabase: CheersDatabase,
    ): CommentDao {
        return cheersDatabase.commentDao()
    }

    @Singleton
    @Provides
    fun provideTicketDao(
        cheersDatabase: CheersDatabase,
    ): TicketDao {
        return cheersDatabase.ticketDao()
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
    ): com.salazar.cheers.data.activity.ActivityDao {
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