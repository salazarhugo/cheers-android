package com.salazar.cheers.shared.data

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BffModule {

    @Singleton
    @Provides
    fun provideBffApiService(
//        authTokenInterceptor: FirebaseUserIdTokenInterceptor,
    ): BffApiService {
        val moshi = Moshi.Builder().build()

        val client = OkHttpClient.Builder().build()

        val okHttpClient: OkHttpClient = client.newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
//            .addInterceptor(authTokenInterceptor)
            .build()

        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BffApiService.GATEWAY_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        return retrofit.create(BffApiService::class.java)
    }
}