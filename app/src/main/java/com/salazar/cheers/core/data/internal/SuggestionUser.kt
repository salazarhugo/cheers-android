package com.salazar.cheers.core.data.internal

data class SuggestionUser(
    val user: User,
    val posts: List<Post> = emptyList(),
)