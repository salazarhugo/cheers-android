package com.salazar.cheers.comment.data

import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.core.data.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Comment data layer.
 */
interface CommentRepository {

    /**
     * Update a comment.
     */
    suspend fun updateComment(
        comment: Comment,
    )

    /**
     * Create a comment on a post.
     */
    suspend fun getComment(
        commentId: String,
    ): Result<Comment>

    /**
     * Create a comment on a post.
     */
    suspend fun createComment(
        postId: String,
        comment: Comment,
        replyToCommentId: String? = null,
    ): Result<Unit>

    /**
     * List post comments.
     */
    suspend fun listComment(postId: String): Flow<Resource<List<Comment>>>

    /**
     * List comment replies.
     */
    suspend fun listReplies(commentId: String): Flow<Resource<List<Comment>>>

    /**
     * Delete a comment
     */
    suspend fun deleteComment(
        commentId: String,
    ): Result<Unit>

    /**
     * Like a comment
     */
    suspend fun likeComment(
        commentId: String,
    ): Result<Unit>

    /**
     * Delete a comment like
     */
    suspend fun deleteLikeComment(
        commentId: String,
    ): Result<Unit>
}