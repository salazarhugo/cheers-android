package com.salazar.cheers.core.domain.model

import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.data.db.entities.UserItem

data class UserWithStories(
    val user: UserItem,
    val stories: List<Story>
)
