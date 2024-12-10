package com.salazar.cheers.data.activity

import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType


fun cheers.activity.v1.Activity.toActivity(): Activity {
    return Activity().copy(
        id = id,
        text = text,
        username = username,
        avatar = picture,
        photoUrl = mediaPicture,
        userId = userId,
        mediaId = mediaId,
        createTime = timestamp,
        type = type.toActivityType()
    )
}

fun cheers.activity.v1.Activity.ActivityType.toActivityType(): ActivityType {
    return when (this) {
        cheers.activity.v1.Activity.ActivityType.POST_LIKED -> ActivityType.POST_LIKE
        cheers.activity.v1.Activity.ActivityType.STORY_LIKED -> ActivityType.STORY_LIKE
        cheers.activity.v1.Activity.ActivityType.FRIEND_ADDED -> ActivityType.FRIEND_ADDED
        cheers.activity.v1.Activity.ActivityType.POST_COMMENTED -> ActivityType.COMMENT
        cheers.activity.v1.Activity.ActivityType.MENTION_POST_CAPTION -> ActivityType.MENTION
        cheers.activity.v1.Activity.ActivityType.MENTION_POST_COMMENT -> ActivityType.MENTION
        cheers.activity.v1.Activity.ActivityType.UNRECOGNIZED -> ActivityType.NONE
        cheers.activity.v1.Activity.ActivityType.COMMENT_LIKED -> ActivityType.COMMENT_LIKED
        cheers.activity.v1.Activity.ActivityType.FOLLOW -> ActivityType.FRIEND_ADDED
        cheers.activity.v1.Activity.ActivityType.PROMOTE_VERIFIED,
        cheers.activity.v1.Activity.ActivityType.PROMOTE_BUSINESS,
        cheers.activity.v1.Activity.ActivityType.PROMOTE_MODERATOR,
        cheers.activity.v1.Activity.ActivityType.PROMOTE_ADMIN -> ActivityType.INFORMATION
    }
}