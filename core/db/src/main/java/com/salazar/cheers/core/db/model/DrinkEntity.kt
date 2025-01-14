package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.Rarity
import com.salazar.cheers.core.model.UserID

@Entity(
    tableName = "drinks",
)
data class DrinkEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val price: Int = 0,
    val lastUsed: Long,
    val userID: UserID = UserID(),
    val brand: String = "",
    val color: String = "",
    val description: String = "",
    val privacy: Privacy = Privacy.PRIVATE,
    val rarity: Rarity = Rarity.DEFAULT,
)

fun DrinkEntity.asExternalModel() = Drink(
    id = id,
    name = name,
    icon = icon,
    price = price,
    lastUsed = lastUsed,
    userID = userID,
    brand = brand,
    description = description,
    privacy = privacy,
    color = color,
    rarity = rarity,
)

fun Drink.asEntity(): DrinkEntity =
    DrinkEntity(
        id = id,
        name = name,
        icon = icon,
        price = price,
        lastUsed = lastUsed,
        userID = userID,
        brand = brand,
        description = description,
        privacy = privacy,
        color = color,
        rarity = rarity,
    )

fun List<DrinkEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Drink>.asEntity() = this.map { it.asEntity() }
