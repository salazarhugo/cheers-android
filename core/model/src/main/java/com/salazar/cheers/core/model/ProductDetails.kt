package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProductDetails(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val formattedPrice: String? = null,
)