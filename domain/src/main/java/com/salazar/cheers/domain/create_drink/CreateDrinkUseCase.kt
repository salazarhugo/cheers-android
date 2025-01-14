package com.salazar.cheers.domain.create_drink

import com.salazar.cheers.data.drink.repository.DrinkRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val DEFAULT_DRINK_ICON = "https://storage.googleapis.com/cheers-drinks/beer.png"

class CreateDrinkUseCase @Inject constructor(
    private val drinkRepository: DrinkRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        name: String,
        icon: String?,
    ) = withContext(dispatcher) {
        return@withContext drinkRepository.createDrink(
            name = name,
            icon = icon ?: DEFAULT_DRINK_ICON,
        )
    }
}