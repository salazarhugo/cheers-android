package com.salazar.cheers.core.model

import java.util.UUID

val emptyUserItem = UserItem(
    id = "",
    username = "",
    name = "",
    verified = false,
    picture = null,
    hasFollowed = false,
    story_state = StoryState.EMPTY,
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
    story_state = StoryState.NOT_SEEN,
    friend = true,
    requested = false,
)

val cheersUser = User(
    id = UUID.randomUUID().toString(),
    username = "cheers",
    name = "Cheers Social",
    verified = true,
    picture = null,
    friend = true,
    requested = false,
)

val cheersUserItemList = List(20) {
    cheersUserItem.copy(id = UUID.randomUUID().toString())
}
