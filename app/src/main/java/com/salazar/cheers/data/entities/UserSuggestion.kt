package com.salazar.cheers.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_suggestion")
data class UserSuggestion(
    @PrimaryKey
    val id: String,
    val name: String,
    val username: String,
    val verified: Boolean,
    val avatar: String,
    val followBack: Boolean,
)