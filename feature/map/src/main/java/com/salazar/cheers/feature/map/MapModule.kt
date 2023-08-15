package com.salazar.cheers.feature.map

import com.salazar.cheers.feature.map.data.repository.MapRepository
import com.salazar.cheers.feature.map.data.repository.MapRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}
