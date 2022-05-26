package com.salazar.cheers.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.internal.User

object StoryType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(tableName = "story")
data class Story(
    @PrimaryKey
    @ColumnInfo(name = "storyId")
    val id: String = "",
    val authorId: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val profilePictureUrl: String = "",
    val seen: Boolean = false,
    val created: Long = 0L,
    val relativeTime: String = "",
    val privacy: String = "",
    val photoUrl: String = "",
    val videoUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val locationName: String = "",
    val tagUsersId: List<String> = emptyList(),
    val type: String = StoryType.TEXT,
)

data class StoryDetail(
    val story: Story,
    val author: User = User(),
    val viewers: List<User> = ArrayList()
)
