package com.salazar.cheers.comment.domain.usecase.create_comment

import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.common.di.IODispatcher
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.post.data.repository.PostRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        postId: String,
        comment: String,
        replyToCommentId: String?,
    ) = withContext(dispatcher) {
        val isReply = replyToCommentId != null
        val user = userRepository.getCurrentUser()

        val localComment = Comment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            text = comment,
            createTime = Date().time / 1000,
            username = user.username,
            verified = user.verified,
            authorId = user.id,
            avatar = user.picture,
            replyToCommentId = replyToCommentId,
            posting = true,
        )

        val result = repository.createComment(
            postId = postId,
            comment = localComment,
            replyToCommentId = replyToCommentId,
        )

        if (result.isFailure || isReply)
            return@withContext


        val post = postRepository.getPost(postId = postId) ?: return@withContext
        postRepository.updatePost(
            post.copy(
                lastCommentUsername = localComment.username,
                lastCommentText = localComment.text,
                lastCommentCreateTime = localComment.createTime,
                comments = post.comments + 1,
            )
        )
    }
}