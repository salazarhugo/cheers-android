package com.salazar.cheers.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val postId: String,
    val prevKey: Int?,
    val nextKey: Int?,
)