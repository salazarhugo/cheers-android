package com.salazar.cheers.domain.list_drink

import com.salazar.cheers.data.drink.repository.DrinkRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListDrinkUseCase @Inject constructor(
    private val drinkRepository: DrinkRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        query: String,
    ): Result<Unit, DataError> {
        return withContext(dispatcher) {
            return@withContext drinkRepository.listDrink(query = query)
        }
    }
}