package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.core.model.ActivityType.NONE
import java.util.UUID


@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey
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

fun ActivityEntity.asExternalModel() = Activity(
    id = id,
    username = username,
    verified = verified,
    createTime = createTime,
    type = type,
    accountId = accountId,
    text = text,
    avatar = avatar,
    acknowledged = acknowledged,
    eventId = eventId,
    followBack = followBack,
    mediaId = mediaId,
    photoUrl = photoUrl,
    userId = userId,
)

fun Activity.asEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        username = username,
        verified = verified,
        createTime = createTime,
        type = type,
        accountId = accountId,
        text = text,
        avatar = avatar,
        acknowledged = acknowledged,
        eventId = eventId,
        followBack = followBack,
        mediaId = mediaId,
        photoUrl = photoUrl,
        userId = userId,
    )
}

fun List<ActivityEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Activity>.asEntity() = this.map { it.asEntity() }
