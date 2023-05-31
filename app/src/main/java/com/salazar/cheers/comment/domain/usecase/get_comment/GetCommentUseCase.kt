package com.salazar.cheers.comment.domain.usecase.get_comment

import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCommentUseCase @Inject constructor(
    private val repository: CommentRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        commentId: String,
    ): Result<Comment> = withContext(dispatcher) {
        return@withContext repository.getComment(commentId = commentId)
    }
}