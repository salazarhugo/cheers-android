package com.salazar.cheers.core.domain.model

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.db.model.Story

data class UserWithStories(
    val user: UserItem,
    val stories: List<Story>
)
