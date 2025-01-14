package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProductDetails(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val formattedPrice: String? = null,
    val offers: List<SubscriptionOfferDetails>? = null,
)

@Immutable
data class SubscriptionOfferDetails(
    val offerToken: String,
    val name: String,
    val formattedPrice: String?,
    val monthlyFormattedPrice: String? = null,
)
