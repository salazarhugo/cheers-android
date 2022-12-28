package com.salazar.cheers.data.repository.comment

import cheers.comment.v1.CommentServiceGrpcKt
import cheers.comment.v1.CreateCommentRequest
import cheers.comment.v1.ListCommentRequest
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.CommentDao
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.toComment
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao,
    private val service: CommentServiceGrpcKt.CommentServiceCoroutineStub,
) : CommentRepository {
    override suspend fun createComment(postId: String, comment: Comment): Result<Unit> {

        commentDao.insert(comment)

        val request = CreateCommentRequest.newBuilder()
            .setComment(comment.text)
            .setPostId(postId)
            .build()

        return try {
            service.createComment(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listComment(postId: String): Flow<Resource<List<Comment>>>
    = flow {
        emit(Resource.Loading(true))

        val localComments = commentDao.listPostComments(postId = postId).first()

        emit(Resource.Success(localComments))

        val request = ListCommentRequest.newBuilder()
            .setPostId(postId)
            .build()

        val remoteComments = try {
            val response = service.listComment(request = request)
            val comments = response.itemsList.map {
                it.toComment()
            }
            comments
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't refresh comments"))
            null
        }

        remoteComments?.let {
            commentDao.insertPostComments(postId, it)
        }

        val localComment = commentDao.listPostComments(postId = postId)
        val all = localComment.map {
            Resource.Success(it)
        }

        emitAll(all)
        emit(Resource.Loading(false))
    }
}