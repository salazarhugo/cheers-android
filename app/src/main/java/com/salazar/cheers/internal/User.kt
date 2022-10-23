package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cheers.type.UserOuterClass
import java.util.*

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val name: String = "",
    val postCount: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val coins: Int = 0,
    val phoneNumber: String = "",
    val verified: Boolean = false,
    val email: String = "",
    val bio: String = "",
    val picture: String? = null,
    val website: String = "",
    val online: Boolean = false,
    val darkMode: Boolean = false,
    val registrationTokens: List<String> = emptyList(),
    val followBack: Boolean = false,
    @ColumnInfo(defaultValue = "false")
    val friend: Boolean = false,
    @ColumnInfo(defaultValue = "EMPTY")
    val storyState: UserOuterClass.StoryState = UserOuterClass.StoryState.EMPTY,
    @ColumnInfo(defaultValue = "false")
    val seenStory: Boolean = false,
    val created: Long = Date().time,
)
