package com.salazar.cheers.internal

data class SuggestionUser(
    val user: User,
    val posts: List<Post> = emptyList(),
)