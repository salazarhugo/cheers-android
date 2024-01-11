package com.salazar.cheers.shared.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FinishRegistrationResponse(
    val status: Int?
)
