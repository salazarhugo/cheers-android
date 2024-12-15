package com.salazar.cheers.shared.data.request

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FinishRegistrationRequest(
    val email: String,
    val userId: String,
    val username: String,
    val passkey: Passkey,
    val challenge: String,
    val device: Device,
)

@JsonClass(generateAdapter = true)
data class Device(
    val name: String,
    val model: String,
)

@JsonClass(generateAdapter = true)
data class Passkey(
    val response: Response,
    val authenticatorAttachment: String,
    val id: String,
    val rawId: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class Response(
    val clientDataJSON: String,
    val attestationObject: String,
    val transports: List<String>
)
