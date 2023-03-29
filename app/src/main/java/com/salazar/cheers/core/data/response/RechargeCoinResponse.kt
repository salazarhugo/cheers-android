package com.salazar.cheers.core.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RechargeCoinResponse(
    val status: Int?
)
