package com.salazar.cheers.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object StoryType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(tableName = "story")
data class StoryResponse(
    @PrimaryKey
    @ColumnInfo(name = "storyId")
    val id: String = "",
    val authorId: String = "",
    val name: String = "",
    val seenBy: List<String> = emptyList(),
    val created: Long = 0L,
    val relativeTime: String = "",
    val privacy: String = "",
    val photos: List<String> = emptyList(),
    val videoUrl: String = "",
    val locationLatitude: Double = 0.0,
    val locationLongitude: Double = 0.0,
    val locationName: String = "",
    val tagUsersId: List<String> = emptyList(),
    val type: String = StoryType.TEXT,
)