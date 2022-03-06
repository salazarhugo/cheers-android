package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    var fullName: String,
    var username: String,
    val postCount: Int,
    val followers: Int,
    val following: Int,
    val coins: Int,
    val phoneNumber: String,
    val verified: Boolean,
    val email: String,
    var bio: String,
    val profilePictureUrl: String,
    val website: String,
    val online: Boolean,
    val darkMode: Boolean,
    val registrationTokens: List<String>,
    val isFollowed: Boolean,
) : Serializable {

    constructor() : this(
        "",
        "",
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
        "defaultPicture/default_profile_picture.jpg",
        "",
        false,
        false,
        listOf(),
        false,
    )
}