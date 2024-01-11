package com.salazar.cheers.domain.list_replies

import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.data.comment.CommentRepository
import com.salazar.common.util.Resource
import com.salazar.common.di.IODispatcher
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