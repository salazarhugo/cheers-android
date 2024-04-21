package com.salazar.cheers.core.model


data class UserSuggestion(
    val id: String,
    val name: String,
    val username: String,
    val verified: Boolean,
    val picture: String?,
    val followBack: Boolean,
    val accountId: String,
)