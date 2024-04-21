package com.salazar.cheers.core.model

data class UserItem(
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val picture: String? = null,
    val hasFollowed: Boolean = false,
    val story_state: StoryState = StoryState.EMPTY,
    val friend: Boolean = false,
    val requested: Boolean = false,
)