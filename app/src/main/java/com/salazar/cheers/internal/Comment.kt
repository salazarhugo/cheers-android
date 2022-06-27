package com.salazar.cheers.internal

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class CommentWithAuthor(
    val author: User = User(),
    val comment: Comment = Comment(),
)

data class Comment(
    val id: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val avatar: String = "",
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val created: Long = 0L,
)