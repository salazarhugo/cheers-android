package com.salazar.cheers.feature.profile.other_profile

import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User

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
        val posts: List<Post>? = null,
        val parties: List<Party>? = null,
        override val isRefreshing: Boolean = false,
        val isLoading: Boolean,
        val errorMessages: List<String>,
    ) : OtherProfileUiState
}