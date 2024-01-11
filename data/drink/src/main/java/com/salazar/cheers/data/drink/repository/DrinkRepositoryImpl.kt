package com.salazar.cheers.data.drink.repository

import cheers.drink.v1.DrinkServiceGrpcKt
import cheers.drink.v1.ListDrinkRequest
import com.salazar.cheers.data.drink.mapper.toDrink
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.data.drink.db.DrinkDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DrinkRepositoryImpl @Inject constructor(
    private val drinkDao: DrinkDao,
    private val service: DrinkServiceGrpcKt.DrinkServiceCoroutineStub,
): DrinkRepository {
    override suspend fun listDrink(): Flow<Result<List<Drink>>> = flow {
        val localDrinks = drinkDao.listDrinks().first()

        // Emit local drinks first
        if (localDrinks.isNotEmpty()) {
            emit(Result.success(localDrinks))
        }

        try {
            val request = ListDrinkRequest.newBuilder().build()
            val response = service.listDrink(request = request)
            val drinks = response.itemsList.map {
                it.toDrink()
            }
            drinkDao.clearAndInsert(drinks = drinks)
            emit(Result.success(drinkDao.listDrinks().first()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}