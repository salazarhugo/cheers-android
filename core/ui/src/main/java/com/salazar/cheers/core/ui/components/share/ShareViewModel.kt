package com.salazar.cheers.core.ui.components.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_friend.ListMyFriendsUseCase
import com.salazar.cheers.domain.send_message.SendMessageUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShareUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val users: List<UserItem> = emptyList(),
    val selectedUsers: List<UserItem> = emptyList(),
)

@HiltViewModel
class ShareViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listMyFriendsUseCase: ListMyFriendsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ShareUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listMyFriendsUseCase().collect(::updateUsers)
        }
    }

    fun updateUsers(users: Resource<List<UserItem>>) {
        when (users) {
            is Resource.Error -> {}
            is Resource.Loading -> {}
            is Resource.Success -> viewModelState.update {
                it.copy(users = users.data.orEmpty())
            }
        }
    }

    fun onSelectUser(user: UserItem) {
        val l = viewModelState.value.selectedUsers.toMutableList()
        if (l.contains(user)) l.remove(user) else l.add(user)
        viewModelState.update {
            it.copy(selectedUsers = l.toList())
        }
    }

    fun onSend(link: String) {
//        viewModelScope.launch {
//            sendMessageUseCase()
//        }
    }
}
