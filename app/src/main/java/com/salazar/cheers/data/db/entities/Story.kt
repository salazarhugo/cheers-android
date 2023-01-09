package com.salazar.cheers.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.internal.User
import java.util.*
import kotlin.collections.ArrayList

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
    val viewed: Boolean = false,
    val liked: Boolean = false,
    val createTime: Long = 0,
    val relativeTime: String = "",
    val privacy: String = "",
    val photo: String = "",
    val videoUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val locationName: String = "",
    val tagUsersId: List<String> = emptyList(),
    val type: String = StoryType.TEXT,
    val accountId: String = "",
)