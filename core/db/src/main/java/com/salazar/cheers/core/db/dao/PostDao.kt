package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.PostEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PostDao {

    @Query("SELECT * FROM posts WHERE (authorId = :userIdOrUsername OR username = :userIdOrUsername) ORDER BY posts.createTime DESC")
    fun getUserPosts(userIdOrUsername: String): Flow<List<PostEntity>>

    /**
     ** Get User posts. Only IMAGE, VIDEO posts.
     **/
    @Transaction
    @Query("SELECT * FROM posts WHERE authorId = :authorId AND type <> 'TEXT' ORDER BY posts.createTime DESC")
    suspend fun getPostsWithAuthorId(authorId: String): List<PostEntity>

    @Transaction
    @Query("SELECT * FROM posts ORDER BY posts.createTime DESC")
    suspend fun getPosts(): List<PostEntity>

    @Query("SELECT * FROM posts ORDER BY posts.createTime DESC")
    fun getPostFeed(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE posts.postId = :postId")
    fun postFlow(postId: String): Flow<PostEntity>

    @Query("SELECT * FROM posts WHERE posts.postId = :postId")
    suspend fun getPost(postId: String): PostEntity?

    @Query("SELECT * FROM posts WHERE createTime > :yesterday")
    fun listMapPost(
        yesterday: Long = (Date().time / 1000) - 24 * 60 * 60,
    ): Flow<List<PostEntity>>

//    @Query("SELECT * FROM posts ORDER BY createTime DESC")
//    fun pagingSource(): PagingSource<Int, com.salazar.cheers.data.post.repository.Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Transaction
    suspend fun insertUserPosts(
        posts: List<PostEntity>
    ) {
        clearAll()
        insertAll(posts)
    }

    @Delete
    suspend fun delete(post: PostEntity)

    @Transaction
    @Query("DELETE FROM posts WHERE posts.authorId = :authorId")
    suspend fun deleteWithAuthorId(authorId: String)

    @Query("DELETE FROM posts WHERE posts.postId = :postId")
    suspend fun deleteWithId(postId: String)

    @Update
    suspend fun update(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}