package com.salazar.cheers.data.drink.repository

import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.shared.util.result.DataError
import kotlinx.coroutines.flow.Flow

interface DrinkRepository {
    suspend fun getDrink(
        drinkID: String,
    ): com.salazar.cheers.shared.util.result.Result<Drink, DataError>

    suspend fun listDrink(
        query: String,
    ): com.salazar.cheers.shared.util.result.Result<Unit, DataError>

    suspend fun listDrinkFlow(): Flow<List<Drink>>

    suspend fun createDrink(
        name: String,
        icon: String,
    ): com.salazar.cheers.shared.util.result.Result<Unit, DataError>
}