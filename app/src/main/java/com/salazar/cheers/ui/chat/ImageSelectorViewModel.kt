package com.salazar.cheers.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.internal.User
import com.salazar.cheers.backend.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageSelectorViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(TagUserViewModelState(isLoading = true))

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

    fun selectUser(user: User) {
        viewModelState.update {
            val selectedUsers = mutableListOf<User>()
            selectedUsers.addAll(it.selectedUsers)

            if (selectedUsers.contains(user))
                selectedUsers.remove(user)
            else
                selectedUsers.add(user)

            return@update it.copy(selectedUsers = selectedUsers)
        }
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

    fun send() {
        if (viewModelState.value.selectedUsers.isEmpty())
            return
        if (viewModelState.value.selectedUsers.size == 1)
            if (viewModelState.value.selectedUsers.size > 1)
                return
    }
}

sealed interface TagUserUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val searchInput: String

    data class NoChannels(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : TagUserUiState

    data class HasChannels(
        val users: List<User>,
        val selectedUsers: List<User>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : TagUserUiState
}

private data class TagUserViewModelState(
    val users: List<User>? = null,
    val selectedUsers: List<User> = mutableListOf(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): TagUserUiState =
        if (users == null) {
            TagUserUiState.NoChannels(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            TagUserUiState.HasChannels(
                users = users,
                selectedUsers = selectedUsers,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

