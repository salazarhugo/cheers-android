package com.salazar.cheers.data.drink.db

import androidx.room.*
import com.salazar.cheers.core.model.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drink: Drink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drinks: List<Drink>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun clearAndInsert(drinks: List<Drink>) {
        clear()
        insert(drinks = drinks)
    }

    @Query("SELECT * FROM drinks ORDER BY name ASC")
    fun listDrinks(): Flow<List<Drink>>

    @Query("SELECT * FROM drinks WHERE id = :drinkID")
    fun getDrink(drinkID: String): Flow<Drink>

    @Query("DELETE FROM drinks")
    suspend fun clear()
}