package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.model.UserItem

@Entity(
    tableName = "user_item",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserItemEntity(
    @PrimaryKey
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val picture: String? = null,
    val has_followed: Boolean = false,
    val story_state: StoryState = StoryState.EMPTY,
    val friend: Boolean = false,
    val requested: Boolean = false,
)

fun UserItemEntity.asExternalModel() = UserItem(
    id = id,
    username = username,
    name = name,
    verified = verified,
    picture = picture,
    hasFollowed = has_followed,
    friend = friend,
    requested = requested,
)

fun UserItem.asEntity(): UserItemEntity {
    return UserItemEntity(
        id = id,
        username = username,
        name = name,
        verified = verified,
        picture = picture,
        has_followed = hasFollowed,
        friend = friend,
        requested = requested,
    )
}

fun List<UserItemEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<UserItem>.asEntity() = this.map { it.asEntity() }
