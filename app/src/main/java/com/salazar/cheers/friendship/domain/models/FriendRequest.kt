package com.salazar.cheers.friendship.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_requests")
data class FriendRequest(
    @PrimaryKey
    val id: String = "",
)