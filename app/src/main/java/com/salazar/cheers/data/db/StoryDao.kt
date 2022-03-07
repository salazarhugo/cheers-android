package com.salazar.cheers.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.salazar.cheers.data.entities.StoryResponse
import com.salazar.cheers.internal.User

@Dao
interface StoryDao {

    @Query("SELECT * FROM story GROUP BY story.authorId ORDER BY story.seenBy DESC")
    fun pagingSource(): PagingSource<Int, Story>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        story: StoryResponse,
        users: List<User>,
    )

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
    val author: User,

    @Relation(
        parentColumn = "tagUsersId",
        entityColumn = "id",
    )
    val tagUsers: List<User> = ArrayList()
)
