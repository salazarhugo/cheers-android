package com.salazar.cheers.domain.list_drink

import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.data.drink.repository.DrinkRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListDrinkUseCase @Inject constructor(
    private val drinkRepository: DrinkRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(): Flow<Result<List<Drink>>> {
        return withContext(dispatcher) {
            return@withContext drinkRepository.listDrink()
        }
    }
}