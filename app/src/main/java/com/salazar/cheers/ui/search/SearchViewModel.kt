package com.salazar.cheers.ui.search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User
import com.salazar.cheers.backend.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private var cheersDao: CheersDao,
) : ViewModel() {

    val name = mutableStateOf("")
    val userRecommendations = mutableStateOf<List<User>>(emptyList())
    val errorMessage = mutableStateOf("")
    val recentUsers = cheersDao.getRecentUsers()
    val query = mutableStateOf("")

    init {
        refreshUserRecommendations()
        queryUsers(query.value)
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
        queryUsers(query)
    }

    val resultUsers = mutableStateOf<List<User>>(emptyList())

    fun queryUsers(query: String) {
        viewModelScope.launch {
            try {
                resultUsers.value = Neo4jUtil.queryUsers(query)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
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
            val result = Neo4jUtil.getUserRecommendations()
            when (result) {
                is Result.Success -> userRecommendations.value = result.data
                is Result.Error -> errorMessage.value = result.exception.toString()
            }
        }
    }
}