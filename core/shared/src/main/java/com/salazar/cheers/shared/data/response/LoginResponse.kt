package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val user: BffUser,
)

@JsonClass(generateAdapter = true)
data class BffUser(
    val id: String?,
    val name: String?,
    val email: String?,
    val verified: Boolean?,
    val username: String?,
    val picture: String?,
    val banner: String?,
    val createTime: String?,
    val isModerator: Boolean?,
)
