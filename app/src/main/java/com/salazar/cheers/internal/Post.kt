package com.salazar.cheers.internal

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
    var verified: Boolean = false,
    var photoPath: String = "",
    var userPhotoUrl: String = "",
    var locationLatitude: Double = 0.0,
    var locationLongitude: Double = 0.0,
    var locationName: String = "",
)
