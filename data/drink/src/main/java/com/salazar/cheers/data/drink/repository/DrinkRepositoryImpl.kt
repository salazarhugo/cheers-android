package com.salazar.cheers.data.drink.repository

import cheers.drink.v1.CreateDrinkRequest
import cheers.drink.v1.DrinkServiceGrpcKt
import cheers.drink.v1.GetDrinkRequest
import cheers.drink.v1.ListDrinkRequest
import cheers.type.Pagination.PaginationRequest
import com.salazar.cheers.core.db.dao.DrinkDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.shared.data.mapper.toDrink
import com.salazar.cheers.shared.data.toDataError
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DrinkRepositoryImpl @Inject constructor(
    private val drinkDao: DrinkDao,
    private val service: DrinkServiceGrpcKt.DrinkServiceCoroutineStub,
) : DrinkRepository {

    override suspend fun getDrink(
        drinkID: String,
    ): Result<Drink, DataError> {
        try {
            val request = GetDrinkRequest.newBuilder()
                .setDrinkId(drinkID)
                .build()
            val response = service.getDrink(request = request)
            val drink = response.drink.toDrink()
            drinkDao.insert(drink.asEntity())

            return Result.Success(drink)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e.toDataError())
        }
    }

    override suspend fun listDrink(
        query: String,
    ): Result<Unit, DataError> {
        try {
            val pagination = PaginationRequest.newBuilder()
                .setPage(0)
                .setPageSize(50)
                .build()
            val request = ListDrinkRequest.newBuilder()
                .setQuery(query)
                .setPagination(pagination)
                .build()
            val response = service.listDrink(request = request)
            val drinks = response.itemsList.map {
                it.toDrink()
            }
            drinkDao.clearAndInsert(drinks = drinks.asEntity())
            return Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e.toDataError())
        }
    }

    override suspend fun listDrinkFlow(): Flow<List<Drink>> {
        return drinkDao.listDrinks()
            .map { it.asExternalModel() }
    }

    override suspend fun createDrink(
        name: String,
        icon: String,
    ): Result<Unit, DataError> {
        try {
            val request = CreateDrinkRequest.newBuilder()
                .setName(name)
                .setIcon(icon)
                .build()

            val response = service.createDrink(request = request)
            val drink = response.drink.toDrink().asEntity()
            drinkDao.insert(drink)
            return Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e.toDataError())
        }
    }
}