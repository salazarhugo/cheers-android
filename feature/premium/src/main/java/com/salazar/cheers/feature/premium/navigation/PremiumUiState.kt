package com.salazar.cheers.feature.premium.navigation

import com.salazar.cheers.core.model.SubscriptionOfferDetails

data class PremiumUiState(
    val isLoading: Boolean,
    val success: Boolean = false,
    val isPremium: Boolean = false,
    val isRefreshing: Boolean = false,
    val plans: List<SubscriptionOfferDetails> = emptyList(),
    val features: List<PremiumFeature> = emptyList(),
    val selectedPlan: SubscriptionOfferDetails? = null,
    val subscriptionProductID: String? = null,
)

