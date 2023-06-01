package com.salazar.cheers.data.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_suggestion")
data class UserSuggestion(
    @PrimaryKey
    val id: String,
    val name: String,
    val username: String,
    val verified: Boolean,
    val picture: String?,
    val followBack: Boolean,
    @ColumnInfo(defaultValue = "")
    val accountId: String,
)