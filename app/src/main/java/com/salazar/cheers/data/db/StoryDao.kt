package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.User
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: Story)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(story: List<Story>)

    @Transaction
    @Query("SELECT * FROM story WHERE authorId = :authorId")
    fun getStoriesByAuthor(authorId: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<Story>>

    @Transaction
    @Query("SELECT * FROM story WHERE username = :username ORDER BY story.createTime")
    suspend fun getUserStory(username: String): List<Story>

    @Query("SELECT * FROM users LIMIT :pageSize OFFSET :skip")
    fun feedStory(skip: Int, pageSize: Int): Flow<List<UserWithStories>>

    @Query("SELECT * FROM story WHERE storyId = :storyId")
    suspend fun getStory(storyId: String): Story?

    @Query("UPDATE story SET viewed = 1  WHERE storyId = :storyId")
    suspend fun viewStory(storyId: String)

    @Query("UPDATE story SET liked = 1  WHERE storyId = :storyId")
    suspend fun likeStory(storyId: String)

    @Query("UPDATE story SET liked = 0  WHERE storyId = :storyId")
    suspend fun unlikeStory(storyId: String)

    @Query("DELETE FROM story WHERE storyId = :storyId")
    suspend fun deleteWithId(storyId: String)

    @Delete
    suspend fun delete(story: Story)

    @Update
    suspend fun update(story: Story)

    @Query("DELETE FROM story")
    suspend fun clearAll()
}

data class UserWithStories(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "authorId",
    )
    val stories: List<Story>
)