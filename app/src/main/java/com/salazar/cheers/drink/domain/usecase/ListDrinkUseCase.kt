package com.salazar.cheers.drink.domain.usecase

import com.salazar.common.di.IODispatcher
import com.salazar.cheers.drink.data.repository.DrinkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ListDrinkUseCase @Inject constructor(
    private val drinkRepository: DrinkRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        return@withContext drinkRepository.listDrink()
    }
}