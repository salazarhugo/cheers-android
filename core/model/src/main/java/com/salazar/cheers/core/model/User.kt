package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class User(
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
    val birthDate: Long = 0L,
    val favouriteDrink: Drink? = null,
    val gender: Gender? = null,
    val jobTitle: String = "",
    val jobCompany: String = "",
    val education: String = "",
    val premium: Boolean = false,
)