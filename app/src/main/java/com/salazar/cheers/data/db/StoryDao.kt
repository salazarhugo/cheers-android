package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.entities.StoryResponse
import com.salazar.cheers.internal.User
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface StoryDao {

    @Transaction
    @Query("SELECT * FROM story WHERE created > :yesterday GROUP BY story.authorId ORDER BY story.seenBy DESC")
    fun pagingSource(yesterday: Long = Date().time - 24 * 60 * 60 * 1000): PagingSource<Int, Story>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: StoryResponse)

    @Transaction
    @Query("SELECT * FROM story WHERE authorId = :authorId")
    fun getStoriesByAuthor(authorId: String = FirebaseAuth.getInstance().currentUser?.uid!!): Flow<List<Story>>

    @Transaction
    @Query("SELECT * FROM story WHERE storyId = :storyId")
    suspend fun getStory(storyId: String): StoryResponse

    @Query("DELETE FROM story WHERE story.storyId = :storyId")
    suspend fun deleteWithId(storyId: String)

    @Delete
    suspend fun delete(story: StoryResponse)

    @Update
    suspend fun update(story: StoryResponse)

    @Query("DELETE FROM story")
    suspend fun clearAll()
}

data class Story(
    @Embedded
    val story: StoryResponse,

    @Relation(parentColumn = "authorId", entityColumn = "id")
    val author: User = User(),

    @Relation(
        parentColumn = "tagUsersId",
        entityColumn = "id",
    )
    val tagUsers: List<User> = ArrayList()
)

data class StoryDetail(
    val story: StoryResponse,
    val author: User = User(),
    val viewers: List<User> = ArrayList()
)
