package com.salazar.cheers.feature.premium.navigation

sealed interface PremiumUiState {

    data object SuccessPurchaseLoading: PremiumUiState

    data class HasOffer(
        val isRefreshing: Boolean,
    ) : PremiumUiState
}

