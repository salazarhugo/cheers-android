package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.internal.ActivityType.*


@Entity(tableName = "activity")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: ActivityType = NONE,
    val userId: String = "",
    val eventId: String = "",
    val photoUrl: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val followBack: Boolean = false,
    val acknowledged: Boolean = false,
    val time: Long = 0L,
    val accountId: String = "",
)

enum class ActivityType {
    NONE,
    FOLLOW,
    POST_LIKE,
    STORY_LIKE,
    COMMENT,
    MENTION,
    CREATE_POST,
    CREATE_EVENT,
    CREATE_STORY,
}

fun ActivityType.toSentence(): String {
    return when(this) {
        NONE -> ""
        FOLLOW -> "started following you."
        POST_LIKE -> "liked your post."
        STORY_LIKE -> "liked your story."
        COMMENT -> "commented on your post."
        MENTION -> "mentioned you."
        CREATE_POST -> "created a post"
        CREATE_EVENT -> TODO()
        CREATE_STORY -> TODO()
    }
}