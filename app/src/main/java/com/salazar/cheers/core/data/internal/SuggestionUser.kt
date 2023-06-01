package com.salazar.cheers.core.data.internal

import com.salazar.cheers.data.user.User

data class SuggestionUser(
    val user: User,
    val posts: List<com.salazar.cheers.data.post.repository.Post> = emptyList(),
)