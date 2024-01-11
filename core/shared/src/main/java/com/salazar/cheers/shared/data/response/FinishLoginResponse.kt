package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FinishLoginResponse(
    val status: Int?,
    val token: String,
    val user: UserResponse,
)
