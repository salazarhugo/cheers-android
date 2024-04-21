package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Theme


@Entity(tableName = "userPreference")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: String,
    val theme: Theme,
)
