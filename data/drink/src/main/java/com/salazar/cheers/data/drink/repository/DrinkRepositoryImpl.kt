package com.salazar.cheers.data.drink.repository

import cheers.drink.v1.DrinkServiceGrpcKt
import cheers.drink.v1.ListDrinkRequest
import com.salazar.cheers.data.drink.mapper.toDrink
import com.salazar.cheers.core.model.Drink
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DrinkRepositoryImpl @Inject constructor(
    private val service: DrinkServiceGrpcKt.DrinkServiceCoroutineStub,
): DrinkRepository {
    override suspend fun listDrink(): Result<List<Drink>> {
        return try {
            val request = ListDrinkRequest.newBuilder().build()
            val response = service.listDrink(request = request)
            val drinks = response.itemsList.map {
                it.toDrink()
            }
            Result.success(drinks)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}