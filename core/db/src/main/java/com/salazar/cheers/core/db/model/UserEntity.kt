package com.salazar.cheers.core.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.salazar.cheers.core.model.Gender
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.model.User
import java.util.UUID

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
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
    val banner: List<String> = emptyList(),
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
    @Embedded(
        prefix = "drink_",
    ) val favouriteDrink: DrinkEntity? = null,
    val premium: Boolean = false,
    val gender: Gender? = null,
    val jobTitle: String = "",
    val jobCompany: String = "",
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
    favouriteDrink = favouriteDrink?.asExternalModel(),
    premium = premium,
    gender = gender,
    jobTitle = jobTitle,
    jobCompany = jobCompany,
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
        favouriteDrink = favouriteDrink?.asEntity(),
        premium = premium,
        gender = gender,
        jobTitle = jobTitle,
        jobCompany = jobCompany,
    )
}

fun List<UserEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<User>.asEntity() = this.map { it.asEntity() }
