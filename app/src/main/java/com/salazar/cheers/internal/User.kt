package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey
    val id: String,
    val username: String,
    val name: String,
    val postCount: Int,
    val followers: Int,
    val following: Int,
    val coins: Int,
    val phoneNumber: String,
    val verified: Boolean,
    val email: String,
    val bio: String,
    val profilePictureUrl: String,
    val website: String,
    val online: Boolean,
    val darkMode: Boolean,
    val registrationTokens: List<String>,
    val followBack: Boolean,
    @ColumnInfo(defaultValue = "false")
    val hasStory: Boolean,
    @ColumnInfo(defaultValue = "false")
    val seenStory: Boolean,
    val created: Long,
) : Serializable {

    constructor() : this(
        "",
        "",
        "",
        0,
        0,
        0,
        0,
        "",
        false,
        "",
        "",
        "",
        "",
        false,
        false,
        listOf(),
        false,
        false,
        false,
        0,
    )
}