package com.salazar.cheers.data.search.di

import com.salazar.cheers.data.search.SearchRepository
import com.salazar.cheers.data.search.SearchRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {

    @Provides
    @Singleton
    fun provideSearchRepository(
        impl: SearchRepositoryImpl,
    ): SearchRepository {
        return impl
    }
}
