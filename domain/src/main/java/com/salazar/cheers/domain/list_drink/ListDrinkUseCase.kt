package com.salazar.cheers.domain.list_drink

import com.salazar.cheers.data.drink.repository.DrinkRepository
import com.salazar.common.di.IODispatcher
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