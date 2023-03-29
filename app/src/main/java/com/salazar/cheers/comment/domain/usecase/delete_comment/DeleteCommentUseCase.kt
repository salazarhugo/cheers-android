package com.salazar.cheers.comment.domain.usecase.delete_comment

import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: CommentRepository,
    private val postRepository: PostRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        commentID: String,
    ) = withContext(dispatcher) {

        val comment = repository.getComment(commentId =  commentID).getOrNull()
            ?: return@withContext

        repository.deleteComment(
            commentId = commentID,
        ).onFailure {
            return@withContext
        }

        val isReply = comment.replyToCommentId != null

        if (isReply)
            return@withContext

        var post = postRepository.getPost(postId = comment.postId) ?: return@withContext

        if (post.lastCommentText == comment.text)
            post = post.copy(lastCommentText = "")

        post = post.copy(comments = post.comments - 1)

        postRepository.updatePost(post)

        return@withContext
    }
}