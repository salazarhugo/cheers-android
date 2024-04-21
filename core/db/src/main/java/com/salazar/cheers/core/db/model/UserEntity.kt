package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.model.User
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String = "",
    val name: String = "",
    val postCount: Int = 0,
    val friendsCount: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val coins: Int = 0,
    val phoneNumber: String = "",
    val verified: Boolean = false,
    val email: String = "",
    val bio: String = "",
    val picture: String? = null,
    val banner: String? = null,
    val website: String = "",
    val online: Boolean = false,
    val darkMode: Boolean = false,
    val registrationTokens: List<String> = emptyList(),
    val followBack: Boolean = false,
    val friend: Boolean = false,
    val requested: Boolean = false,
    val hasRequestedViewer: Boolean = false,
    val storyState: StoryState = StoryState.EMPTY,
    val seenStory: Boolean = false,
    val createTime: Long = 0L,
    val isBusinessAccount: Boolean = false,
)

fun UserEntity.asExternalModel() = User(
    id = id,
    username = username,
    name = name,
    postCount = postCount,
    friendsCount = friendsCount,
    followers = followers,
    following = following,
    coins = coins,
    phoneNumber = phoneNumber,
    verified = verified,
    email = email,
    bio = bio,
    picture = picture,
    banner = banner,
    website = website,
    online = online,
    darkMode = darkMode,
    registrationTokens = registrationTokens,
    followBack = followBack,
    friend = friend,
    requested = requested,
    hasRequestedViewer = hasRequestedViewer,
    storyState = storyState,
    seenStory =seenStory,
    createTime = createTime,
    isBusinessAccount = isBusinessAccount,
)

fun User.asEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        name = name,
        postCount = postCount,
        friendsCount = friendsCount,
        followers = followers,
        following = following,
        coins = coins,
        phoneNumber = phoneNumber,
        verified = verified,
        email = email,
        bio = bio,
        picture = picture,
        banner = banner,
        website = website,
        online = online,
        darkMode = darkMode,
        registrationTokens = registrationTokens,
        followBack = followBack,
        friend = friend,
        requested = requested,
        hasRequestedViewer = hasRequestedViewer,
        storyState = storyState,
        seenStory =seenStory,
        createTime = createTime,
        isBusinessAccount = isBusinessAccount,
    )
}

fun List<UserEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<User>.asEntity() = this.map { it.asEntity() }
