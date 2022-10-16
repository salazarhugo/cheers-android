package com.salazar.cheers.data.mapper

import cheers.post.v1.PostResponse
import cheers.type.PostOuterClass
import com.salazar.cheers.internal.Post


fun PostResponse.toPost(accountId: String): Post {
 return Post().copy(
     id = post.id,
     caption = post.caption,
     username  = user.username,
     verified = user.verified,
     photos = post.photosList,
     profilePictureUrl  = user.picture,
     created = post.createTime.seconds * 1000,
     locationName = post.locationName,
     liked = hasLiked,
     likes = likeCount.toInt(),
     comments = commentCount.toInt(),
     beverage = post.drink,
     drunkenness = post.drunkenness.toInt(),
     latitude= 0.0,
     longitude= 0.0,
     privacy = post.privacy.name,
     accountId = accountId,
    )
}