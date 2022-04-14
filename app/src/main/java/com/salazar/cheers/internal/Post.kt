package com.salazar.cheers.internal

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

object PostType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
        )
    ],
    indices = [
        Index(value = ["authorId"])
    ]
)
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "postId")
    val id: String = "",
    val authorId: String = "",
    val name: String = "",
    val caption: String = "",
    val created: Long = 0,
    val relativeTime: String = "",
    var likes: Int = 0,
    var liked: Boolean = false,
    val duration: String = "",
    val beverage: String = "",
    val comments: Int = 0,
    val shares: Int = 0,
    val privacy: String = "",
    val photos: List<String> = emptyList(),
    val videoUrl: String = "",
    val videoThumbnailUrl: String = "",
    val drunkenness: Int = 0,
    val locationLatitude: Double = 0.0,
    val locationLongitude: Double = 0.0,
    val locationName: String = "",
    val allowJoin: Boolean = true,
    val tagUsersId: List<String> = emptyList(),
    val type: String = PostType.TEXT,
    val accountId: String = "",
)