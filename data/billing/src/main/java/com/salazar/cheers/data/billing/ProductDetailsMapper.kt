package com.salazar.cheers.data.billing

import com.android.billingclient.api.ProductDetails


fun ProductDetails.toProductDetails(): com.salazar.cheers.core.model.ProductDetails {
    return com.salazar.cheers.core.model.ProductDetails(
        id = productId,
        name = name,
        description = description,
        formattedPrice = oneTimePurchaseOfferDetails?.formattedPrice,
    )
}