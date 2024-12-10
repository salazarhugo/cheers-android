package com.salazar.cheers.feature.premium.navigation

import androidx.compose.material3.SheetState
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User

internal data class PremiumViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessages: String = "",
) {
    fun toUiState(): PremiumUiState {
        return when (user != null) {
            true -> PremiumUiState.HasUser(
                posts = posts,
                user = user,
                parties = parties,
                isLoading = isLoading,
                errorMessages = errorMessages,
                isRefreshing = isRefreshing,
            )

            else -> {
                PremiumUiState.NoAccount(
                    isLoading = isLoading,
                    errorMessages = errorMessages,
                    isRefreshing = isRefreshing,
                )
            }
        }
    }
}
