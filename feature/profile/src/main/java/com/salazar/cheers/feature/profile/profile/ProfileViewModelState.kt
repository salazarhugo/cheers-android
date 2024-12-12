package com.salazar.cheers.feature.profile.profile

import androidx.compose.material3.SheetState
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User

internal data class ProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessages: String = "",
) {
    fun toUiState(): ProfileUiState {
        return when (user != null) {
            true -> ProfileUiState.HasUser(
                posts = posts,
                user = user,
                parties = parties,
                isLoading = isLoading,
                errorMessages = errorMessages,
                isRefreshing = isRefreshing,
            )

            else -> {
                ProfileUiState.NoAccount(
                    isLoading = isLoading,
                    errorMessages = errorMessages,
                    isRefreshing = isRefreshing,
                )
            }
        }
    }
}