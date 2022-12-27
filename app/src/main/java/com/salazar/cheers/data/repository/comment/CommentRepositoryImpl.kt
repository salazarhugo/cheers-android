package com.salazar.cheers.data.repository.comment

import cheers.comment.v1.CommentServiceGrpcKt
import cheers.comment.v1.CreateCommentRequest
import cheers.comment.v1.ListCommentRequest
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.toComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val service: CommentServiceGrpcKt.CommentServiceCoroutineStub,
) : CommentRepository {
    override suspend fun createComment(postId: String, comment: String): Result<Unit> {
        val request = CreateCommentRequest.newBuilder()
            .setComment(comment)
            .setPostId(postId)
            .build()

        return try {
            service.createComment(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listComment(postId: String): Flow<List<Comment>> {
        val request = ListCommentRequest.newBuilder()
            .setPostId(postId)
            .build()

        return try {
            val response = service.listComment(request = request)
            val comments = response.itemsList.map { it.toComment() }
            flowOf(comments)
        } catch (e: Exception) {
            emptyFlow()
        }
    }
}