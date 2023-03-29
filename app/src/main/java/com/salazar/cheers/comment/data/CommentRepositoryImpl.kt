package com.salazar.cheers.comment.data

import android.content.res.Resources.NotFoundException
import cheers.comment.v1.*
import com.salazar.cheers.data.Resource
import com.salazar.cheers.comment.data.db.CommentDao
import com.salazar.cheers.comment.data.mapper.toComment
import com.salazar.cheers.comment.domain.models.Comment
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao,
    private val service: CommentServiceGrpcKt.CommentServiceCoroutineStub,
) : CommentRepository {
    override suspend fun updateComment(comment: Comment) {
        return commentDao.update(comment = comment)
    }

    override suspend fun getComment(commentId: String): Result<Comment> {
        val localComment = commentDao.getComment(id = commentId).firstOrNull()
        if (localComment != null)
            return Result.success(localComment)

        return Result.failure(NotFoundException())
    }

    override suspend fun createComment(
        postId: String,
        comment: Comment,
        replyToCommentId: String?,
    ): Result<Unit> {

        commentDao.insert(comment)

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
            commentDao.insert(response.item.toComment())
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

    override suspend fun listReplies(commentId: String): Flow<Resource<List<Comment>>> = flow {
        emit(Resource.Loading(true))

        val localComments = commentDao.listCommentReplies(commentId = commentId).first()

        emit(Resource.Success(localComments))

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
            commentDao.insert(it)
        }

        val localComment = commentDao.listCommentReplies(commentId = commentId)
        val all = localComment.map {
            Resource.Success(it)
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