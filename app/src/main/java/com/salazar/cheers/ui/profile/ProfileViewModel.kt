package com.salazar.cheers.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class Loading(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ProfileUiState

    data class HasUser(
        val user: User,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): ProfileUiState =
        if (user != null)
            ProfileUiState.HasUser(
                user = user,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        else
            ProfileUiState.Loading(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
}

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshUser()
    }

    private fun refreshUser() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getCurrentUser()) {
                    is Result.Success -> it.copy(user = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        errorMessages = listOf(result.exception.toString()),
                        isLoading = false
                    )
                }
            }
        }
    }
}