package com.salazar.cheers.data.mapper

import cheers.story.v1.StoryResponse
import com.salazar.cheers.data.db.entities.Story


fun StoryResponse.toStory(authorId: String, accountId: String): Story {
 return Story().copy(
     id = story.id,
     authorId = authorId,
     viewed = hasViewed,
     liked = hasLiked,
     photo = story.photo,
     created = story.createTime.seconds * 1000,
     locationName = story.locationName,
     latitude= 0.0,
     longitude= 0.0,
     privacy = story.privacy.name,
     accountId = accountId,
    )
}