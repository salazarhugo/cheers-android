package com.salazar.cheers.core.model

import java.util.UUID

val emptyUserItem = UserItem(
    id = "",
    username = "",
    name = "",
    verified = false,
    picture = null,
    hasFollowed = false,
    friend = false,
    requested = false,
)

val cheersUserItem = UserItem(
    id = UUID.randomUUID().toString(),
    username = "cheers",
    name = "Cheers Social",
    verified = true,
    picture = null,
    hasFollowed = false,
    friend = true,
    requested = false,
)

val cheersUser = User(
    id = UUID.randomUUID().toString(),
    username = "cheers",
    name = "Cheers Social",
    verified = true,
    premium = true,
    picture = null,
    friend = true,
    requested = false,
    gender = Gender.MALE,
    jobTitle = "Software Engineer",
    jobCompany = "Google",
    education = "University of Toronto",
    bio = "Cheers is a social media platform for Cheers users.",
    website = "https://cheers.com",
)

val cheersUserItemList = List(20) {
    cheersUserItem.copy(id = UUID.randomUUID().toString())
}
