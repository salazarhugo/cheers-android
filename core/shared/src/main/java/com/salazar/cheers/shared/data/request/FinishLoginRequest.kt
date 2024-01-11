package com.salazar.cheers.shared.data.request

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FinishLoginRequest(
    val username: String,
    val passkey: FinishLoginPasskey,
    val challenge: String,
)

@JsonClass(generateAdapter = true)
data class FinishLoginPasskey(
    val response: FinishLoginResponse,
    val authenticatorAttachment: String,
    val id: String,
    val rawId: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class FinishLoginResponse(
    val clientDataJSON: String,
    val authenticatorData: String,
    val signature: String,
    val userHandle: String,
)
