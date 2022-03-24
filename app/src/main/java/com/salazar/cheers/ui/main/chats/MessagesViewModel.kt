package com.salazar.cheers.ui.main.chats

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.DirectChannel
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val isLoading: Boolean,
    val errorMessages: List<String>,
    val searchInput: String,
    val channels: List<DirectChannel>?,
    val suggestions: List<User>?,
)

private data class MessagesViewModelState(
    val channels: List<DirectChannel>? = null,
    val suggestions: List<User>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): MessagesUiState =
        MessagesUiState(
            channels = channels,
            isLoading = isLoading,
            errorMessages = errorMessages,
            searchInput = searchInput,
            suggestions = suggestions,
        )
}

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()
    val name = mutableStateOf("")

    private val viewModelState =
        MutableStateFlow(MessagesViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshChannels()
        refreshSuggestions()
    }

    private fun refreshSuggestions() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val suggestions = userRepository.getSuggestions()
            viewModelState.update {
                it.copy(suggestions = suggestions, isLoading = false)
            }
        }
    }

    fun onFollowClick(username: String) {
        viewModelScope.launch {
            userRepository.followUser(username = username)
        }
    }

    private fun refreshChannels() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val channels = chatRepository.getChannels()
            viewModelState.update {
                it.copy(channels = channels, isLoading = false)
            }
        }
    }
}