package com.salazar.cheers.ui.main.taguser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.domain.usecase.list_friend.ListFriendUseCase
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
    private val userRepository: UserRepository,
    private val listFriendUseCase: ListFriendUseCase,
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
            listFriendUseCase(FirebaseAuth.getInstance().currentUser?.uid!!).collect(::updateUsers)
        }
    }

    private fun updateUsers(users: List<UserItem>?) {
        viewModelState.update {
            it.copy(users = users)
        }
    }
}