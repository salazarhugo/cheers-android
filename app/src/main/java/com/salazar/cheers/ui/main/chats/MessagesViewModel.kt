package com.salazar.cheers.ui.main.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.Room
import com.salazar.cheers.data.datastore.DataStoreRepository
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val errorMessages: List<String>,
    val searchInput: String,
    val channels: List<ChatChannel>?,
    val suggestions: List<User>?,
)

private data class MessagesViewModelState(
    val channels: List<ChatChannel>? = null,
    val rooms: List<Room>? = null,
    val suggestions: List<User>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): MessagesUiState =
        MessagesUiState(
            channels = channels,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            errorMessages = errorMessages,
            searchInput = searchInput,
            suggestions = suggestions,
        )
}

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
//    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(MessagesViewModelState(isLoading = false))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        onSwipeRefresh()
    }

    fun onSwipeRefresh() {
        viewModelScope.launch() {
            chatRepository.getChannels().collect { channels ->
                viewModelState.update {
                    it.copy(channels = channels, isLoading = false)
                }
            }
        }
    }

    private fun refreshSuggestions() {
//        viewModelState.update { it.copy(isLoading = true, isRefreshing = true) }
//
//        viewModelScope.launch {
//            val suggestions = userRepository.getSuggestions()
//            viewModelState.update {
//                it.copy(suggestions = suggestions, isLoading = false, isRefreshing = false)
//            }
//        }
    }

    fun onFollowToggle(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user = user)
        }
    }
}