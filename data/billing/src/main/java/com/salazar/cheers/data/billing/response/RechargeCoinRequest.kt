package com.salazar.cheers.data.billing.response

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RechargeCoinRequest(
    val packageName: String,
    val productId: String,
    val purchaseToken: String,
)
