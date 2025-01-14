package com.salazar.cheers.core

object PostType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

data class Post(
    val id: String = String(),
    val authorId: String = String(),
    val isAuthor: Boolean = false,
    val caption: String = String(),
    val username: String = String(),
    val name: String = String(),
    val verified: Boolean = false,
    val profilePictureUrl: String = String(),
    val createTime: Long = 0,
    val relativeTime: String = String(),
    val audioUrl: String = String(),
    val audioWaveform: List<Int> = emptyList(),
    val likes: Int = 0,
    val liked: Boolean = false,
    val drinkId: String = String(),
    val drinkName: String = String(),
    val drinkColor: String = String(),
    val drinkPicture: String = String(),
    val comments: Int = 0,
    val shares: Int = 0,
    val privacy: String = String(),
    val photos: List<String> = emptyList(),
    val videoUrl: String = String(),
    val videoThumbnailUrl: String = String(),
    val drunkenness: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = String(),
    val notify: Boolean = true,
    val tagUsersId: List<String> = emptyList(),
    val type: String = PostType.TEXT,
    val accountId: String = String(),
    val lastCommentText: String = String(),
    val lastCommentUsername: String = String(),
    val lastCommentCreateTime: Long = 0,
    val canLike: Boolean = true,
    val canComment: Boolean = true,
    val canShare: Boolean = true,
    val hasMentions: Boolean = false,
    val mentionAvatars: List<String> = emptyList(),
)

