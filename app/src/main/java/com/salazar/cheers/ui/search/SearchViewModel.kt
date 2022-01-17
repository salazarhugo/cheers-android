package com.salazar.cheers.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private var cheersDao: CheersDao,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SearchUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshUserRecommendations()
        cheersDao.getRecentUsers()
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

    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
        refreshUsers(searchInput = searchInput)
    }

    fun refreshUsers(searchInput: String) {
        viewModelScope.launch {
            viewModelState.update {
                try {
                    it.copy(users = Neo4jUtil.queryUsers(searchInput))
                } catch (e: Exception) {
                    Log.e("HomeViewModel", e.toString())
                    it.copy(errorMessage = e.toString())
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
                fullName = user.fullName,
                username = user.username,
                profilePictureUrl = user.profilePictureUrl,
                verified = user.verified,
                date = Instant.now().epochSecond
            )
            cheersDao.insertRecentUser(recentUser)
        }
    }

    private fun refreshUserRecommendations() {
        viewModelScope.launch {
            viewModelState.update {
                val result = Neo4jUtil.getUserRecommendations()
                when (result) {
                    is Result.Success -> it.copy(userRecommendations = result.data)
                    is Result.Error -> it.copy(errorMessage = result.exception.toString())
                }
            }
        }
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

