package com.salazar.cheers.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.SearchResult
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.shared.util.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchResultState {
    data object Uninitialized : SearchResultState
    data object Loading : SearchResultState
    data class SearchResults(val searchResult: SearchResult) :
        SearchResultState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCases: SearchUseCases,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SearchUiState(isLoading = false))
    private var searchJob: Job? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshSuggestions()
        updateRecentUser()
    }

    private fun updateRecentUser() {
        viewModelScope.launch {
            searchUseCases.getRecentSearchUseCase()
                .collect(::updateRecentSearches)
        }
    }

    fun updateRecentSearches(recentSearches: List<RecentSearch>) {
        viewModelState.update {
            it.copy(recentSearch = recentSearches)
        }
    }

    fun onSwipeRefresh() {
        queryUsers(fetchFromRemote = true)
        refreshSuggestions()
    }

    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput, isLoading = true)
        }
        searchJob?.cancel()
        if (searchInput.length <= 2) return
        searchJob = viewModelScope.launch {
            delay(500L)
            queryUsers(query = searchInput)
        }
    }

    private fun queryUsers(
        query: String = uiState.value.searchInput.lowercase(),
        fetchFromRemote: Boolean = true,
    ) {
        updateSearchResultState(SearchResultState.Loading)
        viewModelScope.launch {
            searchUseCases.searchUseCase(
                query = query,
                page = 1,
                pageSize = 10,
            )
                .collect { result ->
                    when (result) {
                        is Result.Error -> Unit
                        is Result.Success -> updateSearchResultState(
                            SearchResultState.SearchResults(searchResult = result.data)
                        )
                    }
                }
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateSearchResultState(searchResultState: SearchResultState) {
        viewModelState.update {
            it.copy(searchResultState = searchResultState)
        }
    }

    fun deleteRecentSearch(search: RecentSearch) {
        viewModelScope.launch {
            searchUseCases.deleteRecentSearchUseCase(search)
        }
    }

    fun toggleFollow(username: String) {
    }

    fun insertRecentParty(party: Party) {
        viewModelScope.launch {
            val recentSearch = RecentSearch.Party(party = party)
            searchUseCases.createRecentUserUseCase(recentSearch)
        }
    }

    fun insertRecentUser(user: UserItem) {
        viewModelScope.launch {
            val recentSearch = RecentSearch.User(user = user)
            searchUseCases.createRecentUserUseCase(recentSearch)
        }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
//            userRepositoryImpl.getSuggestions().collect { suggestions ->
//                viewModelState.update {
//                    it.copy(suggestions = suggestions)
//                }
//            }
        }
    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            val recentSearch = RecentSearch.Text(text = query)
            searchUseCases.createRecentUserUseCase(recentSearch)
        }
    }

    fun onClearRecent() {
        viewModelScope.launch {
            searchUseCases.clearRecentSearchUseCase()
        }
    }
}
