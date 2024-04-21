package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun clear(postId: String)

    @Query("DELETE FROM comments WHERE id = :commentID")
    suspend fun delete(commentID: String)

    @Query("SELECT * FROM comments WHERE postId = :postId AND replyToCommentId IS NULL ORDER BY createTime DESC")
    fun listPostComments(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE replyToCommentId = :commentId ORDER BY createTime DESC")
    fun listCommentReplies(commentId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :id")
    fun getComment(id: String): Flow<CommentEntity>

    @Update
    suspend fun update(comment: CommentEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostComments(postId: String, comments: List<CommentEntity>) {
        clear(postId)
        insert(comments)
    }
}