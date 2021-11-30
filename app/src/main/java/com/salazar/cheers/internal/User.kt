package com.salazar.cheers.internal

import java.io.Serializable


data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    var fullName: String,
    var username: String,
    val posts: Int,
    val followers: Int,
    val following: Int,
    val phoneNumber: String,
    val verified: Boolean,
    val email: String,
    val bio: String,
    val photoUrl: String,
    val website: String,
    val online: Boolean,
    val darkMode: Boolean,
    val registrationTokens: MutableList<String>,
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
        "",
        false,
        "",
        "",
        "defaultPicture/default_profile_picture.jpg",
        "",
        false,
        false,
        mutableListOf(),
    )
}