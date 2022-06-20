package com.salazar.cheers.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import com.salazar.cheers.Settings
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.backend.PublicService
import com.salazar.cheers.data.datastore.dataStore
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.remote.FirebaseUserIdTokenInterceptor
import com.salazar.cheers.data.serializer.SettingsSerializer
import com.salazar.cheers.data.serializer.settingsDataStore
import com.salazar.cheers.internal.PrivacyAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideChatServiceCoroutineStub(): ChatServiceGrpcKt.ChatServiceCoroutineStub {
//
//        //192.168.1.35
//        val managedChannel = ManagedChannelBuilder
//            .forAddress("chat-r3a2dr4u4a-nw.a.run.app", 443)
//            .build()
//
//        val client = ChatServiceGrpcKt
//            .ChatServiceCoroutineStub(managedChannel)
//            .withInterceptors(ErrorHandleInterceptor(""))
////            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header))
//
//        return client
//    }

    @Singleton
    @Provides
    fun providePublicService(): PublicService {

        val moshi = Moshi.Builder().add(PrivacyAdapter()).build()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(PublicService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        return retrofit.create(PublicService::class.java)
    }

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Settings> {
        return context.settingsDataStore
    }

    @Singleton
    @Provides
    fun provideGoApi(): CoreService {

        val moshi = Moshi.Builder().add(PrivacyAdapter()).build()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(FirebaseUserIdTokenInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(CoreService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        return retrofit.create(CoreService::class.java)
    }

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

    @Singleton
    @Provides
    fun provideNeo4jService(): Neo4jService {
        return Neo4jService()
    }
}

@Module
@InstallIn(ViewComponent::class)
object ChatModule