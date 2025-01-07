package com.salazar.cheers.shared.data.mapper

import cheers.type.UserOuterClass
import cheers.user.v1.GetUserResponse
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Gender
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

fun UserOuterClass.User.toUser(
    drink: Drink? = null,
): User {
    return User().copy(
        id = id,
        name = name,
        username = username,
        picture = picture,
        banner = bannersList,
        verified = verified,
        phoneNumber = phoneNumber,
        email = email,
        bio = bio,
        website = website,
        registrationTokens = registrationTokensList,
        createTime = createTime,
        isBusinessAccount = isBusinessAccount,
        favouriteDrink = drink,
        premium = premium,
        gender = gender.toGender(),
        jobTitle = job.title,
        jobCompany = job.company
    )
}

fun UserOuterClass.Gender.toGender(): Gender {
    return when(this) {
        UserOuterClass.Gender.MALE -> Gender.MALE
        UserOuterClass.Gender.FEMALE -> Gender.FEMALE
        UserOuterClass.Gender.UNRECOGNIZED -> Gender.OTHER
    }
}

fun Gender?.toGenderPb(): UserOuterClass.Gender {
    return when(this) {
        Gender.MALE -> UserOuterClass.Gender.MALE
        Gender.FEMALE -> UserOuterClass.Gender.FEMALE
        Gender.OTHER -> UserOuterClass.Gender.MALE
        null -> UserOuterClass.Gender.MALE
    }
}
