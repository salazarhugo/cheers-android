package com.salazar.cheers.data.repository.story

import androidx.paging.PagingData
import cheers.type.StoryOuterClass
import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.db.entities.Story
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Story data layer.
 */
interface StoryRepository {

    /**
     * Create a Cheers story.
     */
    suspend fun createStory(story: StoryOuterClass.Story): Result<Unit>

    /**
     * Get a specific Cheers story.
     */
    suspend fun getStory(storyId: String?): Result<Story>

    /**
     * Get story feed.
     */
    suspend fun feedStory(page: Int, pageSize: Int): Result<List<UserWithStories>>

    /**
     * Get current user stories.
     */
    fun getMyStories(): Flow<List<Story>>

    /**
     * Get stories of a specific user.
     */
    fun getUserStory(username: String): Flow<List<Story>>

    /**
     * Delete a specific Cheers story.
     */
    suspend fun deleteStory(storyId: String)

    /**
     * Mark a specific Cheers story as viewed.
     */
    suspend fun viewStory(storyId: String): Result<Unit>

    /**
     * Like a specific Cheers story.
     */
    suspend fun likeStory(storyId: String)

    /**
     * Unlike a specific Cheers story.
     */
    suspend fun unlikeStory(storyId: String)
}