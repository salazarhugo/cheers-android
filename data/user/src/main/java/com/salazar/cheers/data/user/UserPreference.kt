package com.salazar.cheers.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userPreference")
data class UserPreference(
    @PrimaryKey
    val id: String,
    val theme: Theme
)

enum class Theme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System default")
}
