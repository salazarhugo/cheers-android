package com.salazar.cheers.data.post.repository

import cheers.post.v1.PostResponse
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.db.model.PostEntity


fun PostResponse.toPost(): Post {
    return Post(
        id = post.id,
        authorId = user.id,
        isAuthor = isCreator,
        caption = post.caption,
        username = user.username,
        name = user.name,
        verified = user.verified,
        photos = post.postMediaList.mapNotNull { it.imageVersionsList.firstOrNull()?.url },
        audioUrl = post.audio.url,
        audioWaveform = post.audio.waveformList.map { it.toInt() },
        profilePictureUrl = user.picture,
        createTime = post.createTime,
        locationName = post.locationName,
        liked = hasLiked,
        likes = likeCount.toInt(),
        comments = commentCount.toInt(),
        drinkId = post.drink.id,
        drinkName = post.drink.name,
        drinkColor = post.drink.color,
        drinkPicture = post.drink.icon,
        drunkenness = post.drunkenness.toInt(),
        latitude = post.latitude,
        longitude = post.longitude,
        privacy = post.privacy.name,
        lastCommentText = post.lastCommentText,
        lastCommentUsername = post.lastCommentUsername,
        lastCommentCreateTime = post.lastCommentCreateTime,
        canComment = post.commentsEnabled,
        canLike = post.likesEnabled,
        canShare = post.sharesEnabled,
        hasMentions = post.hasMentions,
        mentionAvatars = post.mentionsList.map { it.picture },
    )
}

fun PostResponse.asEntity(): PostEntity {
    return PostEntity(
        id = post.id,
        authorId = user.id,
        isAuthor = isCreator,
        caption = post.caption,
        username = user.username,
        name = user.name,
        verified = user.verified,
        photos = post.postMediaList.mapNotNull { it.imageVersionsList.firstOrNull()?.url },
        audioUrl = post.audio.url,
        audioWaveform = post.audio.waveformList.map { it.toInt() },
        profilePictureUrl = user.picture,
        createTime = post.createTime,
        locationName = post.locationName,
        liked = hasLiked,
        likes = likeCount.toInt(),
        comments = commentCount.toInt(),
        drinkId = post.drink.id,
        drinkName = post.drink.name,
        drinkColor = post.drink.color,
        drinkPicture = post.drink.icon,
        drunkenness = post.drunkenness.toInt(),
        latitude = post.latitude,
        longitude = post.longitude,
        privacy = post.privacy.name,
        lastCommentText = post.lastCommentText,
        lastCommentUsername = post.lastCommentUsername,
        lastCommentCreateTime = post.lastCommentCreateTime,
        canComment = post.commentsEnabled,
        canLike = post.likesEnabled,
        canShare = post.sharesEnabled,
        hasMentions = post.hasMentions,
        mentionAvatars = post.mentionsList.map { it.picture },
    )
}
