package com.salazar.cheers.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private var cheersDao: CheersDao,
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
        refreshUserRecommendations()
        updateRecentUser()
    }

    private fun updateRecentUser() {
        viewModelScope.launch {
            cheersDao.getRecentUsers().collect { recentUsers ->
                viewModelState.update {
                    it.copy(recentUsers = recentUsers)
                }
            }
        }
    }

    fun onSwipeRefresh() {
        queryUsers(fetchFromRemote = true)
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
            userRepository
                .queryUsers(fetchFromRemote = fetchFromRemote, query = query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                viewModelState.update {
                                    it.copy(users = result.data)
                                }
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            viewModelState.update {
                                it.copy(isLoading = result.isLoading)
                            }
                        }
                    }
                }
        }
    }

    fun deleteRecentUser(user: RecentUser) {
        viewModelScope.launch {
            cheersDao.deleteRecentUser(user)
        }
    }

    fun insertRecentUser(user: User) {
        viewModelScope.launch {
            val recentUser = RecentUser(
                id = user.id,
                fullName = user.name,
                username = user.username,
                profilePictureUrl = user.profilePictureUrl,
                verified = user.verified,
                date = Instant.now().epochSecond
            )
            cheersDao.insertRecentUser(recentUser)
        }
    }

    private fun refreshUserRecommendations() {
//        viewModelScope.launch {
//            viewModelState.update {
//                val result = Neo4jUtil.getUserRecommendations()
//                when (result) {
//                    is Result.Success -> it.copy(userRecommendations = result.data)
//                    is Result.Error -> it.copy(errorMessage = result.exception.toString())
//                }
//            }
//        }
    }
}

data class SearchUiState(
    val name: String = "",
    val users: List<User> = emptyList(),
    val userRecommendations: List<User> = emptyList(),
    val recentUsers: List<RecentUser> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
)

