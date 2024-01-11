package com.salazar.cheers.domain.create_comment

import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.data.comment.CommentRepository
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
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