package com.salazar.cheers.core.data.internal

import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.User

data class SuggestionUser(
    val user: User,
    val posts: List<Post> = emptyList(),
)