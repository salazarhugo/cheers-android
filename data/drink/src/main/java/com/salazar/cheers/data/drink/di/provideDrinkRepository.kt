package com.salazar.cheers.data.drink.di

import com.salazar.cheers.data.drink.repository.DrinkRepository
import com.salazar.cheers.data.drink.repository.DrinkRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DrinkModule {
    @Singleton
    @Provides
    fun provideDrinkRepository(
        drinkRepositoryImpl: DrinkRepositoryImpl,
    ): DrinkRepository {
        return drinkRepositoryImpl
    }
}
