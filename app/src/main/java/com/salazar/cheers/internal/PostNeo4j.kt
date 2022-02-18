package com.salazar.cheers.internal

data class PostNeo4j(
    val id: String = "",
    val authorId: String = "",
    val name: String = "",
    val caption: String = "",
    val type: String = PostType.TEXT,
    val createdTime: String = "",
    var likes: Int = 0,
    var liked: Boolean = false,
    val comments: Int = 0,
    val shares: Int = 0,
    val privacy: String = "",
    val photoUrl: String = "",
    val videoUrl: String = "",
    val tagUsersId: List<String> = emptyList(),
    val videoThumbnailUrl: String = "",
    val locationLatitude: Double = 0.0,
    val locationLongitude: Double = 0.0,
    val locationName: String = "",
)
