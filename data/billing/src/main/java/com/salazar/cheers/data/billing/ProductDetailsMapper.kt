package com.salazar.cheers.data.billing

import com.android.billingclient.api.ProductDetails
import com.salazar.cheers.core.model.SubscriptionOfferDetails
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import java.util.Locale


fun ProductDetails.toProductDetails(): com.salazar.cheers.core.model.ProductDetails {
    return com.salazar.cheers.core.model.ProductDetails(
        id = productId,
        name = title,
        description = description,
        formattedPrice = oneTimePurchaseOfferDetails?.formattedPrice,
        offers = subscriptionOfferDetails?.map { it.toSubscriptionOfferDetails() },
    )
}

fun ProductDetails.SubscriptionOfferDetails.toSubscriptionOfferDetails(): SubscriptionOfferDetails {
    val pricingPhase = pricingPhases.pricingPhaseList.firstOrNull()
    val billingPeriod = pricingPhase?.billingPeriod.orEmpty()
    val priceAmountMicros = pricingPhase?.priceAmountMicros ?: 0
    val priceCurrencyCode = pricingPhase?.priceCurrencyCode.orEmpty()

    return SubscriptionOfferDetails(
        offerToken = offerToken,
        name = toBillingPeriodName(billingPeriod),
        formattedPrice = getFormattedPrice(
            formattedPrice = pricingPhase?.formattedPrice,
            billingPeriod = billingPeriod,
        ),
        monthlyFormattedPrice = getMonthlyFormattedPrice(
            billingPeriod,
            priceAmountMicros,
            priceCurrencyCode
        ),
    )
}

fun getFormattedPrice(
    formattedPrice: String?,
    billingPeriod: String,
): String {
    val suffix = when (billingPeriod) {
        "P1W" -> "week"
        "P1M" -> "month"
        "P3M" -> "3 months"
        "P6M" -> "6 months"
        "P1Y" -> "year"
        else -> ""
    }

    return "$formattedPrice/$suffix"
}

fun getMonthlyFormattedPrice(
    billingPeriod: String,
    priceMicros: Long,
    currencyCode: String
): String {
    // Convert micros to dollars
    val priceDollars = BigDecimal(priceMicros).movePointLeft(6)

    // Calculate monthly price based on billing period
    val monthlyPriceDollars = when (billingPeriod) {
        "P1W" -> priceDollars.multiply(BigDecimal(4))
        "P1M" -> priceDollars
        "P3M" -> priceDollars.divide(BigDecimal(3), 2, RoundingMode.HALF_UP)
        "P6M" -> priceDollars.divide(BigDecimal(6), 2, RoundingMode.HALF_UP)
        "P1Y" -> priceDollars.divide(BigDecimal(12), 2, RoundingMode.HALF_UP)
        else -> BigDecimal.ZERO
    }

    val currencySymbol = try {
        Currency.getInstance(currencyCode).symbol
    } catch (e: Exception) {
        "$"
    }

    // Format the price with currency symbol
    return String.format(Locale.ROOT, "%s%.2f/month", currencySymbol, monthlyPriceDollars)
}

private fun toBillingPeriodName(billingPeriod: String): String {
    return when (billingPeriod) {
        "P1W" -> "Weekly"
        "P1M" -> "Monthly"
        "P3M" -> "Quarterly" // Assuming P3M is considered quarterly
        "P6M" -> "Semi-annually"
        "P1Y" -> "Annual"
        else -> "Unknown"
    }
}