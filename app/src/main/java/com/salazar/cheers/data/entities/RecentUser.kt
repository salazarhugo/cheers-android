package com.salazar.cheers.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recentUsers")
data class RecentUser(
    @PrimaryKey
    val id: String,
    var fullName: String,
    var username: String,
    val verified: Boolean,
    val profilePicturePath: String,
    val date: Long,
)