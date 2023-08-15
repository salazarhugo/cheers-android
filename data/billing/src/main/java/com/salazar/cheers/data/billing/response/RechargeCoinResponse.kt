package com.salazar.cheers.data.billing.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RechargeCoinResponse(
    val status: Int?
)
