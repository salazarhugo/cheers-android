package com.salazar.cheers.comment.data.mapper

import com.salazar.cheers.core.model.Comment

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
        hasLiked = hasLiked,
        likeCount = likeCount.toInt(),
        replyCount = replyCount.toInt(),
        replyToCommentId = comment.replyToCommentId.ifBlank { null },
        posting = false,
    )
}
