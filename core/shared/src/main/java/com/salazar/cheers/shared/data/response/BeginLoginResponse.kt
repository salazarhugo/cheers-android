package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BeginLoginResponse(
    val challenge: String,
    val relyingPartyId: String,
    val userVerification: String,
    val allowCredentials: List<BeginLoginCredential>,
    val timeout: Int,
)

@JsonClass(generateAdapter = true)
data class BeginLoginCredential(
    val id: String,
    val publicKey: String,
    val attestationType: String,
    val transport: List<String>? = null,
)