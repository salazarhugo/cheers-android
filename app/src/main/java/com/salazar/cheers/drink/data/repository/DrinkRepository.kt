package com.salazar.cheers.drink.data.repository

import com.salazar.cheers.drink.domain.models.Drink

interface DrinkRepository {
    suspend fun listDrink(): Result<List<Drink>>
}