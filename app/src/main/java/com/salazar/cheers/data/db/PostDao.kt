package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import java.util.*

@Dao
interface PostDao {

    @Transaction
    @Query("SELECT * FROM posts WHERE accountId = :accountId ORDER BY posts.created DESC")
    fun pagingSourceFeed(accountId: String = FirebaseAuth.getInstance().currentUser?.uid!!): PagingSource<Int, PostFeed>

    @Transaction
    @Query("SELECT posts.* FROM posts JOIN users ON posts.authorId = users.id WHERE (authorId = :userIdOrUsername OR users.username = :userIdOrUsername) ORDER BY posts.created DESC")
    fun profilePostFeed(userIdOrUsername: String): PagingSource<Int, PostFeed>

//    /**
//     ** Get User posts and posts where they are tagged in. Only IMAGE, VIDEO posts.
//     **/
//    @Transaction
//    @Query("SELECT * FROM posts WHERE (authorId = :authorId OR tagUsersId LIKE '%' || :authorId || '%') AND type <> 'TEXT' ORDER BY posts.createdTime DESC")
//    suspend fun getPostsWithAuthorId(authorId: String): List<PostFeed>

    /**
     ** Get User posts. Only IMAGE, VIDEO posts.
     **/
    @Transaction
    @Query("SELECT * FROM posts WHERE authorId = :authorId AND type <> 'TEXT' ORDER BY posts.created DESC")
    suspend fun getPostsWithAuthorId(authorId: String): List<Post>

    @Transaction
    @Query("SELECT posts.* FROM posts JOIN users ON posts.authorId = users.id WHERE username = :username AND type <> 'TEXT' ORDER BY posts.created DESC")
    suspend fun getPostsWithUsername(username: String): List<Post>

//    @Transaction
//    @Query("SELECT users.id FROM users WHERE username = :username")
//    suspend fun getPostsWithAuthorId(authorId: String): List<PostFeed>

    @Transaction
    @Query("SELECT * FROM posts WHERE posts.postId = :postId")
    suspend fun getPost(postId: String): PostFeed

    @Transaction
    @Query("SELECT * FROM posts WHERE privacy = :privacy AND created > :yesterday")
    suspend fun getMapPosts(privacy: Privacy, yesterday: Long = Date().time - 24 * 60 * 60 * 1000): List<PostFeed>

//    @Transaction
//    @Query("SELECT * FROM posts WHERE tagUsersId")
//    suspend fun getUserPosts(userId:): List<PostFeed>

    @Query("SELECT * FROM users WHERE id IN (:tagUsersId)")
    suspend fun getPostUsers(tagUsersId: List<String>): List<User>

    @Query("SELECT * FROM posts ORDER BY created DESC")
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