package com.salazar.cheers.core.model

import com.salazar.cheers.core.model.ActivityType.FRIEND_ADDED
import com.salazar.cheers.core.model.ActivityType.INFORMATION
import com.salazar.cheers.core.model.ActivityType.NONE
import java.util.UUID


data class Activity(
    val id: String = UUID.randomUUID().toString(),
    val type: ActivityType = NONE,
    val userId: String = "",
    val eventId: String = "",
    val avatar: String = "",
    val photoUrl: String = "",
    val text: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val mediaId: String = "",
    val followBack: Boolean = false,
    val acknowledged: Boolean = false,
    val createTime: Long = 0,
    val accountId: String = "",
)

enum class ActivityType {
    NONE,
    FRIEND_ADDED,
    POST_LIKE,
    STORY_LIKE,
    COMMENT,
    COMMENT_LIKED,
    MENTION,
    CREATE_POST,
    CREATE_EVENT,
    CREATE_STORY,
    INFORMATION,
}

val emptyActivity = Activity()
val followActivity = emptyActivity.copy(type = FRIEND_ADDED, username = cheersUserItem.username)
val infoActivity = emptyActivity.copy(type = INFORMATION, text = "Your account has been verified.", username = cheersUserItem.username)
