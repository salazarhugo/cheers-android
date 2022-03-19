package com.salazar.cheers.ui.main.chats

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.ChatChannelType
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val isLoading: Boolean,
    val errorMessages: List<String>,
    val searchInput: String,
    val channels: List<ChatChannel>?,
    val suggestions: List<User>?,
)

private data class MessagesViewModelState(
    val channels: List<ChatChannel>? = null,
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
        refreshMessages()
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

    private fun refreshMessages() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            FirestoreChat.getChatChannels().collect { channels ->
                addDirect(channels)
            }
        }
    }


    private fun addDirect(directChannels: List<ChatChannel>) {
        viewModelScope.launch {
            val channels = mutableListOf<ChatChannel>()

            directChannels.forEach {
                if (it.type == ChatChannelType.GROUP) {
                    channels.add(it)
                } else {
                    val otherUserId =
                        it.members.find { it != FirebaseAuth.getInstance().currentUser?.uid!! }
                            ?: return@forEach

                    val user = userRepository.getUser(userIdOrUsername = otherUserId)
                    channels.add(it.copy(otherUser = user))
                }
            }

            viewModelState.update {
                it.copy(channels = channels, isLoading = false)
            }
        }
    }

}