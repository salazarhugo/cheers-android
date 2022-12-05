package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.internal.ActivityType.*
import java.util.*


@Entity(tableName = "activity")
data class Activity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: ActivityType = NONE,
    val userId: String = "",
    val eventId: String = "",
    val avatar: String = "",
    val photoUrl: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val followBack: Boolean = false,
    val acknowledged: Boolean = false,
    val createTime: Long = 0,
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
    return when (this) {
        NONE -> ""
        FOLLOW -> "started following you."
        POST_LIKE -> "liked your post."
        STORY_LIKE -> "liked your story."
        COMMENT -> "commented on your post."
        MENTION -> "mentioned you."
        CREATE_POST -> "createTime a post"
        CREATE_EVENT -> TODO()
        CREATE_STORY -> TODO()
    }
}