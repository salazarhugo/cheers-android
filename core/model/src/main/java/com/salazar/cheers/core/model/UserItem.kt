package com.salazar.cheers.core.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_item",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserItem(
    @PrimaryKey
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val picture: String? = null,
    val has_followed: Boolean = false,
    val story_state: StoryState = StoryState.EMPTY,
    val friend: Boolean = false,
    val requested: Boolean = false,
)