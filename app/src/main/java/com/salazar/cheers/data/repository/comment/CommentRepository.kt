package com.salazar.cheers.data.repository.comment

import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Party
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Comment data layer.
 */
interface CommentRepository {

    /**
     * Create a comment on a post.
     */
    suspend fun createComment(postId: String, comment: String): Result<Unit>

    /**
     * List post comments.
     */
    suspend fun listComment(postId: String): Flow<List<Comment>>
}