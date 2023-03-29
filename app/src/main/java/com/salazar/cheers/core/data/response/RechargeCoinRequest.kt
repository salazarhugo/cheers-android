package com.salazar.cheers.core.data.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RechargeCoinRequest(
    val packageName: String,
    val productId: String,
    val purchaseToken: String,
)
