package com.salazar.cheers.comment.domain.usecase.like_comment

import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LikeCommentUseCase @Inject constructor(
    private val repository: CommentRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        commentId: String,
    ) = withContext(dispatcher) {

        val comment = repository.getComment(commentId = commentId).getOrNull()
            ?: return@withContext


        if (comment.hasLiked) {
            repository.updateComment(comment = comment.copy(
                hasLiked = false,
                likeCount = comment.likeCount - 1,
            ))
            repository.deleteLikeComment(commentId = commentId)
        }
        else {
            repository.updateComment(comment = comment.copy(
                hasLiked = true,
                likeCount = comment.likeCount + 1,
            ))
            repository.likeComment(commentId = commentId)
        }

        return@withContext
    }
}