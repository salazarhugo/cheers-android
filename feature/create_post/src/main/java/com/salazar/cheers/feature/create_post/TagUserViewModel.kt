package com.salazar.cheers.feature.create_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_friend.ListMyFriendsUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AddPeopleUiState(
    val isLoading: Boolean,
    val users: List<UserItem>? = null,
    val selectedUsers: List<UserItem> = emptyList(),
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
)

@HiltViewModel
class AddPeopleViewModel @Inject constructor(
    private val listFriendUseCase: ListMyFriendsUseCase,
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
            listFriendUseCase().collect(::updateUsers)
        }
    }

    private fun updateUsers(users: Resource<List<UserItem>>) {
        when (users) {
            is Resource.Error -> {
                viewModelState.update {
                    it.copy(isLoading = false, errorMessages = listOf())
                }
            }

            is Resource.Loading -> {
                viewModelState.update {
                    it.copy(isLoading = true)
                }
            }

            is Resource.Success -> {
                viewModelState.update {
                    it.copy(isLoading = false, users = users.data)
                }
            }
        }
    }
}