package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey
    val id: String = "",
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
    val profilePictureUrl: String = "",
    val website: String = "",
    val online: Boolean = false,
    val darkMode: Boolean = false,
    val registrationTokens: List<String> = emptyList(),
    val followBack: Boolean = false,
    @ColumnInfo(defaultValue = "false")
    val friend: Boolean = false,
    @ColumnInfo(defaultValue = "EMPTY")
    val storyState: StoryState = StoryState.EMPTY,
    @ColumnInfo(defaultValue = "false")
    val seenStory: Boolean = false,
    val created: Long = 0L,
)

enum class StoryState {
    EMPTY,
    NOT_SEEN,
    SEEN,
    LOADING,
}