package com.salazar.cheers.feature.profile.profile

import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User

sealed interface ProfileUiState {

    data object Loading : ProfileUiState

    data object NotSignIn : ProfileUiState

    data class HasUser(
        val user: User,
        val posts: List<Post>?,
        val parties: List<Party>?,
        val isLoading: Boolean,
        val errorMessages: String,
        val isRefreshing: Boolean,
    ) : ProfileUiState
}

