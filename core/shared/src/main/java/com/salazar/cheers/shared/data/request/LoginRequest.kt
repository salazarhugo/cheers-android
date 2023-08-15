package com.salazar.cheers.shared.data.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val idToken: String,
)
