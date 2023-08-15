package com.salazar.cheers.data.user

import cheers.user.v1.GetUserResponse

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
        friend = friend,
        requested = requested,
        hasRequestedViewer = hasRequestedViewer,
        friendsCount = friendsCount,
    )
}

fun cheers.type.UserOuterClass.User.toUser(): User {
    return User().copy(
        id = id,
        name = name,
        username = username,
        picture = picture,
        banner = banner,
        verified = verified,
        phoneNumber = phoneNumber,
        email = email,
        bio = bio,
        website = website,
        registrationTokens = registrationTokensList,
        createTime = createTime,
        isBusinessAccount = isBusinessAccount,
    )
}
