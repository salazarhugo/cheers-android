package com.salazar.cheers.data.post.repository

import androidx.room.*
import com.salazar.cheers.core.model.Privacy
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PostDao {

    @Query("SELECT * FROM posts WHERE (authorId = :userIdOrUsername OR username = :userIdOrUsername) ORDER BY posts.createTime DESC")
    fun getUserPosts(userIdOrUsername: String): Flow<List<Post>>

    /**
     ** Get User posts. Only IMAGE, VIDEO posts.
     **/
    @Transaction
    @Query("SELECT * FROM posts WHERE authorId = :authorId AND type <> 'TEXT' ORDER BY posts.createTime DESC")
    suspend fun getPostsWithAuthorId(authorId: String): List<Post>

    @Transaction
    @Query("SELECT * FROM posts ORDER BY posts.createTime DESC")
    suspend fun getPosts(): List<Post>

    @Query("SELECT * FROM posts ORDER BY posts.createTime DESC")
    fun getPostFeed(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE posts.postId = :postId")
    fun postFlow(postId: String): Flow<Post>

    @Query("SELECT * FROM posts WHERE posts.postId = :postId")
    suspend fun getPost(postId: String): Post?

    @Query("SELECT * FROM posts WHERE createTime > :yesterday")
    fun listMapPost(
        yesterday: Long = (Date().time / 1000) - 24 * 60 * 60,
    ): Flow<List<Post>>

//    @Query("SELECT * FROM posts ORDER BY createTime DESC")
//    fun pagingSource(): PagingSource<Int, com.salazar.cheers.data.post.repository.Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    @Transaction
    suspend fun insertUserPosts(
        posts: List<Post>
    ) {
        clearAll()
        insertAll(posts)
    }

    @Delete
    suspend fun delete(post: Post)

    @Transaction
    @Query("DELETE FROM posts WHERE posts.authorId = :authorId")
    suspend fun deleteWithAuthorId(authorId: String)

    @Query("DELETE FROM posts WHERE posts.postId = :postId")
    suspend fun deleteWithId(postId: String)

    @Update
    suspend fun update(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}