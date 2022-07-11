package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.entities.Story
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface StoryDao {

    @Transaction
    @Query("SELECT * FROM story WHERE created > :yesterday GROUP BY story.authorId ORDER BY story.created DESC")
    fun pagingSource(yesterday: Long = Date().time - 24 * 60 * 60 * 1000): PagingSource<Int, Story>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: Story)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(story: List<Story>)

    @Transaction
    @Query("SELECT * FROM story WHERE authorId = :authorId")
    fun getStoriesByAuthor(authorId: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<Story>>

    @Transaction
    @Query("SELECT * FROM story WHERE username = :username ORDER BY story.created")
    suspend fun getUserStory(username: String): List<Story>

    @Transaction
    @Query("SELECT * FROM story WHERE storyId = :storyId")
    suspend fun getStory(storyId: String): Story

    @Query("DELETE FROM story WHERE story.storyId = :storyId")
    suspend fun deleteWithId(storyId: String)

    @Delete
    suspend fun delete(story: Story)

    @Update
    suspend fun update(story: Story)

    @Query("DELETE FROM story")
    suspend fun clearAll()
}

