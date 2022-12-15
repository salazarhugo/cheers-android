package com.salazar.cheers.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.salazar.cheers.data.enums.StoryState

@Entity(
    tableName = "user_item",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserItem(
    @PrimaryKey
    val id: String,
    val username: String,
    val name: String,
    val verified: Boolean,
    val picture: String?,
    val has_followed: Boolean,
    val story_state: StoryState
)