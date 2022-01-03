package com.salazar.cheers.internal

data class Post(
    val id: String = "",
    val caption: String = "",
    val createdTime: String = "",
    var likes: Int = 0,
    var liked: Boolean = false,
    val comments: Int = 0,
    val shares: Int = 0,
    val creator: User = User(),
    val showOnMap: Boolean = false,
    val photoPath: String = "",
    val videoPath: String = "",
    val locationLatitude: Double = 0.0,
    val locationLongitude: Double = 0.0,
    val locationName: String = "",
    val tagUsers: List<User> = emptyList(),
)
