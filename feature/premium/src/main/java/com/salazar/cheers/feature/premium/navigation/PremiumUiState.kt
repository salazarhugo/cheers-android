package com.salazar.cheers.feature.premium.navigation

import androidx.compose.material3.SheetState
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User

sealed interface PremiumUiState {

    val isLoading: Boolean
    val isRefreshing: Boolean
    val errorMessages: String
    val sheetState: SheetState

    data class NoAccount(
        override val isLoading: Boolean,
        override val errorMessages: String,
        override val sheetState: SheetState,
        override val isRefreshing: Boolean,
    ) : PremiumUiState

    data class HasUser(
        val user: User,
        val posts: List<Post>?,
        val parties: List<Party>?,
        override val sheetState: SheetState,
        override val isLoading: Boolean,
        override val errorMessages: String,
        override val isRefreshing: Boolean,
    ) : PremiumUiState
}

