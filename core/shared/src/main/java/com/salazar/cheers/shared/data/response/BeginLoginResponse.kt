package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BeginLoginResponse(
    val publicKey: PublicKeyCredential,
)

@JsonClass(generateAdapter = true)
data class PublicKeyCredential(
    val user: PublicKeyCredentialUser,
    val challenge: String,
    val rp: ReplyingParty,
    val userVerification: String,
    val allowCredentials: List<BeginLoginCredential>,
    val timeout: Int,
)

@JsonClass(generateAdapter = true)
data class ReplyingParty(
    val id: String,
    val name: String,
)

@JsonClass(generateAdapter = true)
data class PublicKeyCredentialUser(
    val id: String,
    val displayName: String
)

@JsonClass(generateAdapter = true)
data class BeginLoginCredential(
    val id: String,
    val publicKey: String,
    val attestationType: String,
    val transport: List<String>? = null,
)