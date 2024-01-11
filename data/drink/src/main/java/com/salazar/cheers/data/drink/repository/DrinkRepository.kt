package com.salazar.cheers.data.drink.repository

import com.salazar.cheers.core.model.Drink
import kotlinx.coroutines.flow.Flow

interface DrinkRepository {
    suspend fun listDrink(): Flow<Result<List<Drink>>>
}