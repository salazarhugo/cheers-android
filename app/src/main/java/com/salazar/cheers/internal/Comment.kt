package com.salazar.cheers.internal

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class CommentWithAuthor(
    val author: User = User(),
    val comment: Comment = Comment(),
) {
}

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    @ServerTimestamp
    val created: Date = Date(),
) {
}