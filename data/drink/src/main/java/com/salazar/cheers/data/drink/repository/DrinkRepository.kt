package com.salazar.cheers.data.drink.repository

import com.salazar.cheers.core.model.Drink

interface DrinkRepository {
    suspend fun listDrink(): Result<List<Drink>>
}