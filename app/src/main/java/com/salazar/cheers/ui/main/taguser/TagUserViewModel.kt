package com.salazar.cheers.ui.main.taguser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPeopleViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(AddPeopleViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshNewMessages()
    }

    fun onSearchInputChanged(query: String) {
        viewModelState.update {
            it.copy(searchInput = query)
        }
        refreshNewMessages(query = query)
    }

    private fun refreshNewMessages(query: String = "") {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                val result = Neo4jUtil.queryFriends(query)
//                when(result)
                it.copy(users = result, isLoading = false)
            }
        }
    }
}


data class AddPeopleUiState(
    val isLoading: Boolean,
    val users: List<User>?,
    val selectedUsers: List<User>,
    val errorMessages: List<String>,
    val searchInput: String
)

private data class AddPeopleViewModelState(
    val users: List<User>? = null,
    val selectedUsers: List<User> = mutableListOf(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): AddPeopleUiState =
        AddPeopleUiState(
            users = users,
            selectedUsers = selectedUsers,
            isLoading = isLoading,
            errorMessages = errorMessages,
            searchInput = searchInput
        )
}

