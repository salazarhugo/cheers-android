package com.salazar.cheers.core.data.internal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson


enum class Privacy(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
) {
    PRIVATE(
        title = "Private",
        subtitle = "Only people who are invited",
        Icons.Filled.Lock,
    ),
    PUBLIC(
        title = "Public",
        subtitle = "Anyone on Cheers",
        icon = Icons.Filled.Public,
    ),
    FRIENDS(
        title = "Friends",
        subtitle = "Your friends on Cheers",
        icon = Icons.Filled.People,
    ),
    GROUP(
        title = "Group",
        subtitle = "Members of a group that you're in",
        icon = Icons.Filled.Groups,
    ),
}

class PrivacyAdapter {
    @ToJson
    fun toJson(value: Privacy): String = value.name

    @FromJson
    fun fromJson(value: String): Privacy = Privacy.valueOf(value)
}