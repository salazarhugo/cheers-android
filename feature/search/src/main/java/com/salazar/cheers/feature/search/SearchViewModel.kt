package com.salazar.cheers.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.RecentUser
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.core.model.UserSuggestion
import com.salazar.common.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SearchUiState(
    val name: String = "",
    val users: List<com.salazar.cheers.core.model.UserItem>? = null,
    val suggestions: List<UserSuggestion> = emptyList(),
    val recentUsers: List<RecentUser> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SearchUiState(isLoading = true))
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
        queryUsers(fetchFromRemote = false)
    }

    private fun updateRecentUser() {
        viewModelScope.launch {
//            cheersDao.getRecentUsers().collect { recentUsers ->
//                viewModelState.update {
//                    it.copy(recentUsers = recentUsers)
//                }
//            }
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
        searchJob = viewModelScope.launch {
            delay(500L)
            queryUsers(query = searchInput)
        }
    }

    private fun queryUsers(
        query: String = uiState.value.searchInput.lowercase(),
        fetchFromRemote: Boolean = true,
    ) {
        viewModelScope.launch {
            userRepositoryImpl
                .queryUsers(fetchFromRemote = fetchFromRemote, query = query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> updateUsers(users = result.data)
                        is Resource.Loading -> updateIsLoading(result.isLoading)
                        is Resource.Error -> Unit
                    }
                }
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateUsers(users: List<com.salazar.cheers.core.model.UserItem>?) {
        viewModelState.update {
            it.copy(users = users)
        }
    }

    fun deleteRecentUser(user: RecentUser) {
//        viewModelScope.launch {
//            cheersDao.deleteRecentUser(user)
//        }
    }

    fun toggleFollow(username: String) {
    }

    fun insertRecentUser(username: String) {
        viewModelScope.launch {
            userRepositoryImpl.insertRecent(username)
        }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            userRepositoryImpl.getSuggestions().collect { suggestions ->
                viewModelState.update {
                    it.copy(suggestions = suggestions)
                }
            }
        }
    }
}
