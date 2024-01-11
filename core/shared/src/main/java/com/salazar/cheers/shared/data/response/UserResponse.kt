package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    val id: String,
    val username: String,
    val name: String?,
    val bio: String?,
    val email: String?,
    val banner: String?,
    val website: String?,
    val picture: String?,
    val isAdmin: Boolean?,
    val birthday: String?,
    val verified: Boolean?,
    val createTime: String?,
    val phoneNumber: String?,
    val isModerator: Boolean?,
)
