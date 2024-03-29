package com.salazar.cheers.feature.profile.other_profile

import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.user.User

sealed interface OtherProfileUiState {

    val username: String
    val isRefreshing: Boolean

    data class Loading(
        override val username: String,
        override val isRefreshing: Boolean = false,
    ) : OtherProfileUiState

    data class NotFound(
        override val username: String,
        override val isRefreshing: Boolean = false,
    ): OtherProfileUiState

    data class HasUser(
        override val username: String,
        val user: User,
        val posts: List<com.salazar.cheers.data.post.repository.Post>? = null,
        val parties: List<Party>? = null,
        override val isRefreshing: Boolean = false,
        val isLoading: Boolean,
        val errorMessages: List<String>,
    ) : OtherProfileUiState
}