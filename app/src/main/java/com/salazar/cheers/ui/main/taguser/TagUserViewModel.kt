package com.salazar.cheers.ui.main.taguser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AddPeopleUiState(
    val isLoading: Boolean,
    val users: List<User>? = null,
    val selectedUsers: List<User> = emptyList(),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
)

@HiltViewModel
class AddPeopleViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(AddPeopleUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshFriends()
    }

    fun onSearchInputChanged(query: String) {
        viewModelState.update {
            it.copy(searchInput = query)
        }
        refreshFriends(query = query)
    }

    private fun refreshFriends(query: String = "") {
        viewModelScope.launch {
            userRepository.queryUsers(fetchFromRemote = true, query = query).collect {
                if (it is Resource.Success)
                    updateUsers(it.data)
            }
        }
    }

    private fun updateUsers(users: List<User>?) {
        viewModelState.update {
            it.copy(users = users)
        }
    }
}