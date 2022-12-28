package com.salazar.cheers.domain.usecase.create_comment

import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.comment.CommentRepository
import com.salazar.cheers.di.IODispatcher
import com.salazar.cheers.internal.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: CommentRepository,
    private val userRepository: UserRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        postId: String,
        comment: String,
    ): Result<Unit> = withContext(dispatcher) {
        val user = userRepository.getCurrentUser()

        val localComment = Comment(
            postId = postId,
            text = comment,
            createTime = Date().time / 1000,
            username = user.username,
            verified = user.verified,
            authorId = user.id,
            avatar = user.picture,
        )

        return@withContext repository.createComment(
            postId = postId,
            comment = localComment,
        )
    }
}