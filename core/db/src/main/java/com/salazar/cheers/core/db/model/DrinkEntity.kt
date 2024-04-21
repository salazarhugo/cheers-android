package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Drink

@Entity(
    tableName = "drinks",
)
data class DrinkEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val category: String,
)

fun DrinkEntity.asExternalModel() = Drink(
    id = id,
    name = name,
    icon = icon,
    category = category,
)

fun Drink.asEntity(): DrinkEntity =
    DrinkEntity(
        id = id,
        name = name,
        icon = icon,
        category = category,
    )

fun List<DrinkEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Drink>.asEntity() = this.map { it.asEntity() }
