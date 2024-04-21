package com.salazar.cheers.core.model


data class RecentUser(
    val id: String,
    var fullName: String,
    var username: String,
    val verified: Boolean,
    val profilePictureUrl: String?,
    val date: Long,
)