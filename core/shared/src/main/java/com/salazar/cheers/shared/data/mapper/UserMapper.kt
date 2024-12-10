package com.salazar.cheers.shared.data.mapper

import cheers.user.v1.GetUserResponse
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.User

fun RecentSearch.User.toUser(): User {
    return User().copy(
        id = user.id,
        name = user.name,
        username = user.username,
        picture = user.picture,
        verified = user.verified,
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
        favouriteDrink = if (hasDrink()) drink.toDrink() else null,
    )
}

fun cheers.type.UserOuterClass.User.toUser(
    drink: Drink? = null,
): User {
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
        favouriteDrink = drink,
    )
}
