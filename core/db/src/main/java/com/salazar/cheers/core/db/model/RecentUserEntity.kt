package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.RecentUser


@Entity(tableName = "recentUsers")
data class RecentUserEntity(
    @PrimaryKey
    val id: String,
    var fullName: String,
    var username: String,
    val verified: Boolean,
    val profilePictureUrl: String?,
    val date: Long,
)

fun RecentUserEntity.asExternalModel() = RecentUser(
    id = id,
    fullName = fullName,
    username = username,
    verified = verified,
    profilePictureUrl = profilePictureUrl,
    date = date,
)
