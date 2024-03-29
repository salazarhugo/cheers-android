package com.salazar.cheers.data.remote_config.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.salazar.cheers.data.remote_config.BuildConfig
import com.salazar.cheers.data.remote_config.RemoteConfigRepository
import com.salazar.cheers.data.remote_config.RemoteConfigRepositoryImpl
import com.salazar.cheers.data.remote_config.get_remote_config.RemoteConfigCheckFeatureEnabledUseCase
import com.salazar.cheers.domain.get_remote_config.CheckFeatureEnabledUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {

    @Singleton
    @Provides
    fun provideRemoteConfigRepository(
        repository: RemoteConfigRepositoryImpl,
    ): RemoteConfigRepository {
        return repository
    }

    @Singleton
    @Provides
    fun provideRemoteConfigUseCase(
        usecase: RemoteConfigCheckFeatureEnabledUseCase,
    ): CheckFeatureEnabledUseCase {
        return usecase
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfigService(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig

        if (BuildConfig.DEBUG) {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 30
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }

        return remoteConfig
    }
}
