package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.DrinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drink: DrinkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drinks: List<DrinkEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun clearAndInsert(drinks: List<DrinkEntity>) {
        clear()
        insert(drinks = drinks)
    }

    @Query("SELECT * FROM drinks ORDER BY name ASC")
    fun listDrinks(): Flow<List<DrinkEntity>>

    @Query("SELECT * FROM drinks WHERE id = :drinkID")
    fun getDrink(drinkID: String): Flow<DrinkEntity>

    @Query("DELETE FROM drinks")
    suspend fun clear()
}