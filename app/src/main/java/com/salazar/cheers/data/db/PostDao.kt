package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User

@Dao
interface PostDao {

    @Transaction
    @Query("SELECT * FROM posts INNER JOIN users ON posts.authorId = users.id ORDER BY posts.createdTime DESC")
    fun pagingSourceFeed(): PagingSource<Int, PostFeed>

    @Query("SELECT * FROM posts INNER JOIN users ON posts.authorId = users.id WHERE users.id = :authorId ORDER BY posts.createdTime DESC")
    fun getPostsWithAuthorId(authorId: String): List<PostFeed>

    @Query("SELECT * FROM posts INNER JOIN users ON posts.authorId = users.id WHERE posts.postId = :postId")
    fun getPost(postId: String): PostFeed

    @Query("SELECT * FROM posts ORDER BY createdTime DESC")
    fun pagingSource(): PagingSource<Int, Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        user: User,
        post: Post
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    @Delete
    suspend fun delete(post: Post)

    @Query("DELETE FROM posts WHERE posts.postId = :postId")
    suspend fun deleteWithId(postId: String)

    @Update
    suspend fun update(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}

data class PostFeed(
    @Embedded
    val post: Post,
    @Embedded
    val author: User,
)