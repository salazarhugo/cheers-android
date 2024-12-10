package com.salazar.cheers.data.comment

import android.content.res.Resources.NotFoundException
import cheers.comment.v1.CommentServiceGrpcKt
import cheers.comment.v1.CreateCommentRequest
import cheers.comment.v1.CreateLikeCommentRequest
import cheers.comment.v1.DeleteCommentRequest
import cheers.comment.v1.DeleteLikeCommentRequest
import cheers.comment.v1.ListCommentRequest
import cheers.comment.v1.ListRepliesRequest
import com.salazar.cheers.comment.data.mapper.toComment
import com.salazar.cheers.core.db.dao.CommentDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao,
    private val service: CommentServiceGrpcKt.CommentServiceCoroutineStub,
) : CommentRepository {
    override suspend fun updateComment(comment: Comment) {
        return commentDao.update(comment = comment.asEntity())
    }

    override suspend fun getComment(commentId: String): Result<Comment> {
        val localComment = commentDao.getComment(id = commentId).firstOrNull()
        if (localComment != null)
            return Result.success(localComment.asExternalModel())

        return Result.failure(NotFoundException())
    }

    override suspend fun createComment(
        postId: String,
        comment: Comment,
        replyToCommentId: String?,
    ): Result<Unit> {

        commentDao.insert(comment.asEntity())

        val request = CreateCommentRequest.newBuilder()
            .setPostId(postId)
            .setComment(comment.text)
            .apply {
                if (replyToCommentId != null)
                    setReplyToCommentId(replyToCommentId)
            }
            .build()

        return try {
            val response = service.createComment(request = request)
            commentDao.delete(commentID = comment.id)
            commentDao.insert(response.item.toComment().asEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            commentDao.delete(commentID = comment.id)
            Result.failure(e)
        }
    }

    override suspend fun listComment(postId: String): Flow<Resource<List<Comment>>>
    = flow {
        emit(Resource.Loading(true))

        val localComments = commentDao.listPostComments(postId = postId).first()

        if (localComments.isNotEmpty()) {
            emit(Resource.Success(localComments.asExternalModel()))
        }

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
            commentDao.insertPostComments(postId, it.asEntity())
        }

        val localComment = commentDao.listPostComments(postId = postId)
        val all = localComment.map {
            Resource.Success(it.asExternalModel())
        }

        emitAll(all)
        emit(Resource.Loading(false))
    }

    override suspend fun listReplies(commentId: String): Flow<Resource<List<Comment>>> = flow {
        emit(Resource.Loading(true))

        val localComments = commentDao.listCommentReplies(commentId = commentId).first()

        emit(Resource.Success(localComments.asExternalModel()))

        val request = ListRepliesRequest.newBuilder()
            .setCommentId(commentId)
            .build()

        val remoteComments = try {
            val response = service.listReplies(request = request)
            val comments = response.itemsList.map {
                it.toComment()
            }
            comments
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Couldn't refresh comments"))
            null
        }

        remoteComments?.let {
            commentDao.insert(it.asEntity())
        }

        val localComment = commentDao.listCommentReplies(commentId = commentId)
        val all = localComment.map {
            Resource.Success(it.asExternalModel())
        }

        emitAll(all)
        emit(Resource.Loading(false))
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        val request = DeleteCommentRequest.newBuilder()
            .setCommentId(commentId)
            .build()

        return try {
            service.deleteComment(request = request)
            commentDao.delete(commentID = commentId)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun likeComment(commentId: String): Result<Unit> {
        val request = CreateLikeCommentRequest.newBuilder()
            .setCommentId(commentId)
            .build()

        return try {
            service.createLikeComment(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteLikeComment(commentId: String): Result<Unit> {
        val request = DeleteLikeCommentRequest.newBuilder()
            .setCommentId(commentId)
            .build()

        return try {
            service.deleteLikeComment(request = request)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}