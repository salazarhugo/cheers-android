package com.salazar.cheers.core.domain.model

import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.core.model.UserItem

data class UserWithStories(
    val user: com.salazar.cheers.core.model.UserItem,
    val stories: List<Story>
)
