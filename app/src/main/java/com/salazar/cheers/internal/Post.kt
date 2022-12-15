package com.salazar.cheers.internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cheers.type.UserOuterClass

object PostType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(
    tableName = "posts",
//    foreignKeys = [
//        ForeignKey(
//            entity = User::class,
//            parentColumns = ["id"],
//            childColumns = ["authorId"],
//            onDelete = CASCADE,
//            onUpdate = CASCADE,
//        )
//    ],
    indices = [
        Index(value = ["authorId"])
    ]
)
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "postId")
    val id: String = "",
    val authorId: String = "",
    val caption: String = "",
    val username: String = "",
//    val storyState: UserOuterClass.StoryState = UserOuterClass.StoryState.EMPTY,
    val verified: Boolean = false,
    val profilePictureUrl: String = "",
    val createTime: Long = 0,
    val relativeTime: String = "",
    val likes: Int = 0,
    val liked: Boolean = false,
    val beverage: String = "",
    val comments: Int = 0,
    val shares: Int = 0,
    val privacy: String = "",
    val photos: List<String> = emptyList(),
    val videoUrl: String = "",
    val videoThumbnailUrl: String = "",
    val drunkenness: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val notify: Boolean = true,
    val tagUsersId: List<String> = emptyList(),
    val type: String = PostType.TEXT,
    val accountId: String = "",
)