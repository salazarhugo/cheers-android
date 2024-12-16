package com.salazar.cheers.feature.premium.navigation

internal data class PremiumViewModelState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val success: Boolean = false,
) {
    fun toUiState(): PremiumUiState {
        return when (success) {
            false -> PremiumUiState.HasOffer(
                isRefreshing = isRefreshing,
            )

            true -> PremiumUiState.SuccessPurchaseLoading
        }
    }
}
