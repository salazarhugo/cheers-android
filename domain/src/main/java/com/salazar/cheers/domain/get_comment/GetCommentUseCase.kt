package com.salazar.cheers.domain.get_comment

import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.data.comment.CommentRepository
import com.salazar.cheers.shared.di.IODispatcher
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