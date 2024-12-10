package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BeginRegistrationResponse(
    val publicKey: PublicKeyCredential,
)

