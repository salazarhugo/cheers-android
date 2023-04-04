package com.salazar.cheers.comment.domain.usecase.list_replies

import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.core.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListRepliesUseCase @Inject constructor(
    private val repository: CommentRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        commentId: String,
    ): Flow<Resource<List<Comment>>> = withContext(dispatcher) {
        return@withContext repository.listReplies(commentId = commentId)
    }
}