package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.UserStats

@Entity(
    tableName = "user_stats",
    indices = [Index(value = ["username"], unique = true)],
)
data class UserStatsEntity(
    @PrimaryKey
    val id: String = "",
    val username: String = "",
    val drinks: Int = 0,
    val maxDrunkenness: Float = 0f,
    val avgDrunkenness: Float = 0f,
    val favoriteDrink: String = "",
)

fun UserStatsEntity.asExternalModel() = UserStats(
    id = id,
    username = username,
    drinks = drinks,
    maxDrunkenness = maxDrunkenness,
    avgDrunkenness = avgDrunkenness,
    favoriteDrink = favoriteDrink,
)

fun UserStats.asEntity(): UserStatsEntity {
    return UserStatsEntity(
        id = id,
        username = username,
        drinks = drinks,
        maxDrunkenness = maxDrunkenness,
        avgDrunkenness = avgDrunkenness,
        favoriteDrink = favoriteDrink,
    )
}

fun List<UserStatsEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<UserStats>.asEntity() = this.map { it.asEntity() }
