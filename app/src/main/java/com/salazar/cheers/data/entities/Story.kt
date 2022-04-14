package com.salazar.cheers.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.salazar.cheers.internal.User

object StoryType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
    const val VIDEO = "VIDEO"
}

@Entity(
    tableName = "story",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
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
    val photoUrl: String = "",
    val videoUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val locationName: String = "",
    val tagUsersId: List<String> = emptyList(),
    val type: String = StoryType.TEXT,
)