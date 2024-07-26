package com.salazar.cheers.domain.delete_comment

import com.salazar.cheers.data.comment.CommentRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: CommentRepository,
    private val postRepository: com.salazar.cheers.data.post.repository.PostRepository,
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