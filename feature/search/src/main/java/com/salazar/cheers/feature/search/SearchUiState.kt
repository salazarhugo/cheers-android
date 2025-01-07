package com.salazar.cheers.feature.search

import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.UserSuggestion


data class SearchUiState(
    val name: String = "",
    val searchResultState: SearchResultState = SearchResultState.Uninitialized,
    val suggestions: List<UserSuggestion> = emptyList(),
    val recentSearch: List<RecentSearch> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
)
