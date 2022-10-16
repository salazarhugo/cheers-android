package com.salazar.cheers.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.salazar.cheers.internal.Beverage

@Entity(
    tableName = "user_stats",
    indices = [Index(value = ["username"], unique = true)],
)
data class UserStats(
    @PrimaryKey
    val id: String = "",
    val username: String = "",
    val drinks: Int = 0,
    val maxDrunkenness: Float = 0f,
    val avgDrunkenness: Float = 0f,
    val favoriteDrink: Beverage = Beverage.NONE,
)