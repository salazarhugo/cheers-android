package com.salazar.cheers.internal


data class CommentWithAuthor(
    val author: User = User(),
    val comment: Comment = Comment(),
)

data class Comment(
    val id: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val avatar: String? = null,
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val createTime: Long = 0L,
)