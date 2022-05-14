package com.salazar.cheers.ui.main.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.data.mapper.toUser
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.addOrRemove
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewChatUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val groupName: String = "",
    val query: String = "",
    val isGroup: Boolean = false,
    val selectedUsers: Set<User> = emptySet(),
    val recentUsers: List<User> = emptyList(),
    val users: List<User> = emptyList(),
)

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(NewChatUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            userRepository.getRecentUsers().collect { recentUsers ->
                onRecentUsersChange(recentUsers = recentUsers)
            }
        }
    }

    private fun onRecentUsersChange(recentUsers: List<RecentUser>) {
        viewModelState.update {
            it.copy(recentUsers = recentUsers.map { it.toUser() })
        }
    }

    fun onFabClick(onSuccess: (String) -> Unit) {
        val state = uiState.value

        if (state.selectedUsers.isEmpty()) return

        if (state.selectedUsers.size > 1 && state.groupName.isBlank())
            onErrorMessageChange(errorMessage = "Group name can't be blank")
        else
            viewModelScope.launch {
                val roomId = chatRepository.createGroupChat(
                    state.groupName,
                    state.selectedUsers.map { it.id })
                onSuccess(roomId)
            }
    }

    private fun onErrorMessageChange(errorMessage: String) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    fun onNewGroupClick() {
        viewModelState.update {
            it.copy(isGroup = true)
        }
    }

    fun onGroupNameChange(groupName: String) {
        viewModelState.update {
            it.copy(groupName = groupName)
        }
    }

    fun onUserCheckedChange(user: User) {
        viewModelState.update {
            val set = it.selectedUsers.toMutableSet()
            set.addOrRemove(user)
            it.copy(selectedUsers = set.toSet())
        }
    }

    fun onQueryChange(query: String) {
        viewModelState.update {
            it.copy(query = query)
        }
        queryUsers(query = query)
    }

    private fun queryUsers(
        query: String = uiState.value.query.lowercase(),
        fetchFromRemote: Boolean = false,
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
}

