package com.salazar.cheers.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UserItem(
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val picture: String? = null,
    val hasFollowed: Boolean = false,
    val friend: Boolean = false,
    val requested: Boolean = false,
)