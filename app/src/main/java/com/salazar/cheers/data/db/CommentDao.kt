package com.salazar.cheers.data.db

import androidx.room.*
import com.salazar.cheers.internal.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comments: List<Comment>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun clear(postId: String)

    @Query("SELECT * FROM comments WHERE postId = :postId")
    fun listPostComments(postId: String): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE id = :id")
    fun getComment(id: String): Flow<Comment>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostComments(postId: String, comments: List<Comment>) {
        clear(postId)
        insert(comments)
    }
}