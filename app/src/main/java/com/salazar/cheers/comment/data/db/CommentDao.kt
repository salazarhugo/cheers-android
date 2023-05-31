package com.salazar.cheers.comment.data.db

import androidx.room.*
import com.salazar.cheers.comment.domain.models.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comments: List<Comment>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun clear(postId: String)

    @Query("DELETE FROM comments WHERE id = :commentID")
    suspend fun delete(commentID: String)

    @Query("SELECT * FROM comments WHERE postId = :postId AND replyToCommentId IS NULL ORDER BY createTime DESC")
    fun listPostComments(postId: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE replyToCommentId = :commentId ORDER BY createTime DESC")
    fun listCommentReplies(commentId: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE id = :id")
    fun getComment(id: String): Flow<Comment>

    @Update
    suspend fun update(comment: Comment)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostComments(postId: String, comments: List<Comment>) {
        clear(postId)
        insert(comments)
    }
}