package com.salazar.cheers.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT * FROM drinks ORDER BY lastUsed DESC")
    fun listDrinks(): Flow<List<DrinkEntity>>

    @Query("SELECT * FROM drinks WHERE id = :drinkID")
    fun getDrink(drinkID: String): Flow<DrinkEntity>

    @Query("DELETE FROM drinks")
    suspend fun clear()
}