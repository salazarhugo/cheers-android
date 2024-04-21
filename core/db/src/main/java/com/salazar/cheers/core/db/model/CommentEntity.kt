package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Comment

@Entity(tableName = "comments")
data class CommentEntity(
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

fun CommentEntity.asExternalModel() = Comment(
    id = id,
    authorId = authorId,
    username = username,
    verified = verified,
    createTime = createTime,
    avatar = avatar,
    hasLiked = hasLiked,
    likeCount = likeCount,
    postId = postId,
    posting = posting,
    replyCount = replyCount,
    replyToCommentId = replyToCommentId,
    text = text,
)

fun Comment.asEntity() = CommentEntity(
    id = id,
    authorId = authorId,
    username = username,
    verified = verified,
    createTime = createTime,
    avatar = avatar,
    hasLiked = hasLiked,
    likeCount = likeCount,
    postId = postId,
    posting = posting,
    replyCount = replyCount,
    replyToCommentId = replyToCommentId,
    text = text,
)

fun List<CommentEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Comment>.asEntity() = this.map { it.asEntity() }
