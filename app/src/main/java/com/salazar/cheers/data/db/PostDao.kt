package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User

@Dao
interface PostDao {

    @Transaction
    @Query("SELECT * FROM posts ORDER BY posts.createdTime DESC")
    fun pagingSourceFeed(): PagingSource<Int, PostFeed>

    @Transaction
    @Query("SELECT * FROM posts WHERE (authorId = :authorId OR tagUsersId LIKE '%' || :authorId || '%') AND type <> 'TEXT' ORDER BY posts.createdTime DESC")
    suspend fun getPostsWithAuthorId(authorId: String): List<PostFeed>

    @Transaction
    @Query("SELECT * FROM posts WHERE posts.postId = :postId")
    suspend fun getPost(postId: String): PostFeed

    @Transaction
    @Query("SELECT * FROM posts WHERE privacy = :privacy")
    suspend fun getMapPosts(privacy: Privacy): List<PostFeed>

//    @Transaction
//    @Query("SELECT * FROM posts WHERE tagUsersId")
//    suspend fun getUserPosts(userId:): List<PostFeed>

    @Query("SELECT * FROM users WHERE id IN (:tagUsersId)")
    suspend fun getPostUsers(tagUsersId: List<String>): List<User>

    @Query("SELECT * FROM posts ORDER BY createdTime DESC")
    fun pagingSource(): PagingSource<Int, Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        post: Post,
        users: List<User>,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

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

data class PostFeed(
    @Embedded
    val post: Post,

    @Relation(parentColumn = "authorId", entityColumn = "id")
    val author: User,

    @Relation(
        parentColumn = "tagUsersId",
        entityColumn = "id",
    )
    val tagUsers: List<User> = ArrayList()
)