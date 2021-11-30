package com.salazar.cheers.internal

import com.google.type.DateTime

data class Post(
    val id: String = "",
    var userId: String = "",
    val caption: String = "",
    val createdTime: String = "",
    var likes: Int = 0,
    var liked: Boolean = false,
    val comments: Int = 0,
    val shares: Int = 0,
    var username: String = "",
    var photoUrl: String = "",
)
