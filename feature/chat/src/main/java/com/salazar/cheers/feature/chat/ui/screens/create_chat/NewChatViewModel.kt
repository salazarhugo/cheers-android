package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.addOrRemove
import com.salazar.cheers.domain.list_friend.ListMyFriendsUseCase
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.util.Resource
import com.salazar.cheers.shared.util.result.Result
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
    val selectedUsers: Set<UserItem> = emptySet(),
    val users: List<UserItem>? = emptyList(),
)

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val listFriendUseCase: ListMyFriendsUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(NewChatUiState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listFriendUseCase().collect { result ->
                when (result) {
                    is Resource.Error -> {}
                    is Resource.Loading -> updateIsLoading(isLoading = result.isLoading)
                    is Resource.Success -> onRecentUsersChange(users = result.data)
                }
            }
        }
    }

    private fun onRecentUsersChange(users: List<UserItem>?) {
        viewModelState.update {
            it.copy(users = users)
        }
    }

    fun onCreateChat(onSuccess: (String) -> Unit) {
        val state = uiState.value

        if (state.selectedUsers.isEmpty())
            return

        if (state.selectedUsers.size > 1 && state.groupName.isBlank()) {
            onErrorMessageChange(errorMessage = "Group name can't be blank")
            return
        }

        updateIsLoading(true)

        viewModelScope.launch {
            val result = chatRepository.getOrCreateGroupChat(
                groupName = state.groupName,
                UUIDs = state.selectedUsers.map { it.id },
            )
            when(result) {
                is Result.Error -> {
                    onErrorMessageChange(result.error.name)
                }
                is Result.Success -> {
                }
            }
            updateIsLoading(false)
        }
    }

    private fun onErrorMessageChange(errorMessage: String) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
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

    fun onUserCheckedChange(user: UserItem) {
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
//            userRepository
//                .queryUsers(fetchFromRemote = fetchFromRemote, query = query)
//                .collect { result ->
//                    when (result) {
//                        is Resource.Success -> {
//                            result.data?.let {
//                                viewModelState.update {
//                                    it.copy(users = result.data)
//                                }
//                            }
//                        }
//                        is Resource.Error -> Unit
//                        is Resource.Loading -> {
//                            viewModelState.update {
//                                it.copy(isLoading = result.isLoading)
//                            }
//                        }
//
//                        else -> {}
//                    }
//                }
        }
    }
}

