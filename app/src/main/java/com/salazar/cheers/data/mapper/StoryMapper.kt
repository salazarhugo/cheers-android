package com.salazar.cheers.data.mapper

import cheers.story.v1.StoryResponse
import com.salazar.cheers.core.db.model.Story


fun StoryResponse.toStory(authorId: String, accountId: String): Story {
 return Story().copy(
     id = story.id,
     authorId = authorId,
     viewed = hasViewed,
     liked = hasLiked,
     photo = story.photo,
     createTime = story.createTime,
     locationName = story.locationName,
     latitude= 0.0,
     longitude= 0.0,
     privacy = story.privacy.name,
     accountId = accountId,
    )
}