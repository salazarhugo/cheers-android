package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.Story
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
    fun getStoriesByAuthor(authorId: String = String()): Flow<List<Story>>

    @Transaction
    @Query("SELECT * FROM story WHERE accountId = :accountId AND createTime > :yesterday ORDER BY createTime")
    fun listStory(
        accountId: String = String(),
        yesterday: Long = (Date().time / 1000) - 60 * 60 * 24
    ): Flow<List<Story>>

    @Query("SELECT * FROM story WHERE storyId = :storyId")
    suspend fun getStory(storyId: String): Story?

    @Query("UPDATE story SET viewed = 1 WHERE storyId = :storyId")
    suspend fun viewStory(storyId: String)

    @Query("UPDATE story SET liked = 1 WHERE storyId = :storyId")
    suspend fun likeStory(storyId: String)

    @Query("UPDATE story SET liked = 0 WHERE storyId = :storyId")
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