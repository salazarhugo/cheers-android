package com.salazar.cheers.feature.map

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.salazar.cheers.feature.map.data.repository.MapRepository
import com.salazar.cheers.feature.map.data.repository.MapRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    fun provideMapRepository(
        impl: MapRepositoryImpl,
    ): MapRepository {
        return impl
    }

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Singleton
    @Provides
    fun provideDefaultLocationClient(
        @ApplicationContext context: Context,
        client: FusedLocationProviderClient,
    ): com.salazar.cheers.feature.map.location.DefaultLocationClient {
        return com.salazar.cheers.feature.map.location.DefaultLocationClient(context, client)
    }

}
