package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey


data class CommentWithAuthor(
    val author: User = User(),
    val comment: Comment = Comment(),
)

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
)

fun cheers.comment.v1.CommentItem.toComment(): Comment {
    return Comment(
        id = comment.id,
        text = comment.text,
        postId = comment.postId,
        createTime = comment.createTime,
        avatar = userItem.picture,
        authorId = userItem.id,
        verified = userItem.verified,
        username = userItem.username,
    )
}