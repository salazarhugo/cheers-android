package com.salazar.cheers.domain.list_post_comments

import com.salazar.cheers.data.comment.CommentRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListPostCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(postID: String) = withContext(dispatcher) {
        return@withContext commentRepository.listComment(postId = postID)
    }
}