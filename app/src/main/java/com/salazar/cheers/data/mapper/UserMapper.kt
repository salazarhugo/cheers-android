package com.salazar.cheers.data.mapper

import cheers.user.v1.GetUserResponse
import com.salazar.cheers.data.db.entities.RecentUser
import com.salazar.cheers.internal.User

fun RecentUser.toUser(): User {
    return User().copy(
        id = id,
        name = fullName,
        username = username,
        picture = profilePictureUrl,
        verified = verified,
    )
}

fun GetUserResponse.toUser(): User {
    return user.toUser().copy(
        postCount = postCount,
        followBack = hasFollowed,
        followers = followersCount,
        following = followingCount,
    )
}

fun cheers.type.UserOuterClass.User.toUser(): User {
    return User().copy(
        id = id,
        name = name,
        username = username,
        picture = picture,
        verified = verified,
        phoneNumber = phoneNumber,
        email = email,
        bio = bio,
        website = website,
        registrationTokens = registrationTokensList,
        created = createTime.seconds,
    )
}
