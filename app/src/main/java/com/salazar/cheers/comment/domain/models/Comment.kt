package com.salazar.cheers.comment.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val avatar: String? = null,
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val createTime: Long = 0L,
    val likeCount: Int = 0,
    val hasLiked: Boolean = false,
    val replyCount: Int = 0,
    val replyToCommentId: String? = null,
    val posting: Boolean = false,
)