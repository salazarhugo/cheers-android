package com.salazar.cheers.core.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.PostType

@Entity(
    tableName = "posts",
    indices = [
        Index(value = ["authorId"])
    ]
)
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name = "postId")
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
)

fun PostEntity.asExternalModel() = Post(
    id = id,
    authorId = authorId,
    isAuthor = isAuthor,
    caption = caption,
    username = username,
    name = name,
    verified = verified,
    profilePictureUrl = profilePictureUrl,
    createTime = createTime,
    relativeTime = relativeTime,
    audioUrl = audioUrl,
    audioWaveform = audioWaveform,
    likes = likes,
    liked = liked,
    drinkId = drinkId,
    drinkName = drinkName,
    drinkPicture = drinkPicture,
    comments = comments,
    shares = shares,
    privacy = privacy,
    photos = photos,
    videoUrl = videoUrl,
    videoThumbnailUrl = videoThumbnailUrl,
    drunkenness = drunkenness,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    notify = notify,
    tagUsersId = tagUsersId,
    type = type,
    accountId = accountId,
    lastCommentText = lastCommentText,
    lastCommentUsername = lastCommentUsername,
    lastCommentCreateTime = lastCommentCreateTime,
)

fun Post.asEntity(): PostEntity {
    return PostEntity(
        id = id,
        authorId = authorId,
        isAuthor = isAuthor,
        caption = caption,
        username = username,
        name = name,
        verified = verified,
        profilePictureUrl = profilePictureUrl,
        createTime = createTime,
        relativeTime = relativeTime,
        audioUrl = audioUrl,
        audioWaveform = audioWaveform,
        likes = likes,
        liked = liked,
        drinkId = drinkId,
        drinkName = drinkName,
        drinkPicture = drinkPicture,
        comments = comments,
        shares = shares,
        privacy = privacy,
        photos = photos,
        videoUrl = videoUrl,
        videoThumbnailUrl = videoThumbnailUrl,
        drunkenness = drunkenness,
        latitude = latitude,
        longitude = longitude,
        locationName = locationName,
        notify = notify,
        tagUsersId = tagUsersId,
        type = type,
        accountId = accountId,
        lastCommentText = lastCommentText,
        lastCommentUsername = lastCommentUsername,
        lastCommentCreateTime = lastCommentCreateTime,
    )
}

fun List<PostEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Post>.asEntity() = this.map { it.asEntity() }
