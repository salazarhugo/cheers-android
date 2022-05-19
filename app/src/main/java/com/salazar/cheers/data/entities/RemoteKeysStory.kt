package com.salazar.cheers.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_remote_keys")
data class StoryRemoteKey(
    @PrimaryKey
    val storyId: String,
    val prevKey: Int?,
    val nextKey: Int?,
)

@Entity(tableName = "event_remote_keys")
data class EventRemoteKey(
    @PrimaryKey
    val eventId: String,
    val prevKey: Int?,
    val nextKey: Int?,
)
