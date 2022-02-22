package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object PostType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "postId")
    val id: String = "",
    val authorId: String = "",
    val name: String = "",
    val caption: String = "",
    val createdTime: String = "",
    val relativeTime: String = "",
    var likes: Int = 0,
    var liked: Boolean = false,
    val duration: String = "",
    val comments: Int = 0,
    val shares: Int = 0,
    val privacy: String = "",
    val photos: List<String> = emptyList(),
    val videoUrl: String = "",
    val videoThumbnailUrl: String = "",
    val locationLatitude: Double = 0.0,
    val locationLongitude: Double = 0.0,
    val locationName: String = "",
    val allowJoin: Boolean = true,
    val tagUsersId: List<String> = emptyList(),
    val type: String = PostType.TEXT,
)