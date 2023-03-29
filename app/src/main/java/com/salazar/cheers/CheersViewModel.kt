package com.salazar.cheers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.db.entities.Theme
import com.salazar.cheers.data.db.entities.UserPreference
import com.salazar.cheers.data.repository.BillingRepository
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import javax.inject.Inject

data class CheersUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
    val user: User? = null,
    val userPreference: UserPreference = UserPreference(id = "", theme = Theme.SYSTEM),
    val unreadChatCount: Int = 0,
)

@HiltViewModel
class CheersViewModel @Inject constructor(
    webSocket: WebSocket,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CheersUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            chatRepository.getUnreadChatCount().collect { unreadChatCount ->
                viewModelState.update {
                    it.copy(unreadChatCount = unreadChatCount)
                }
            }
        }
        webSocket.send("Hello")
    }

    fun onAuthChange(auth: FirebaseAuth) {
        Log.i("AUTH1", auth.currentUser?.uid.toString())

        if (auth.currentUser == null) {
            viewModelState.update {
                it.copy(user = null, isLoading = false)
            }
            return
        }

        viewModelState.update {
            it.copy(isLoading = true)
        }

        getAndSaveRegistrationToken()

        viewModelScope.launch {
            chatRepository.getUnreadChatCount().collect { unreadChatCount ->
                viewModelState.update {
                    it.copy(unreadChatCount = unreadChatCount)
                }
            }
        }

        viewModelScope.launch {
            chatRepository.getInbox()
        }

        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect(::updateUser)
        }
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user, isLoading = false)
        }
    }

    private fun getAndSaveRegistrationToken() {
        if (FirebaseAuth.getInstance().currentUser == null)
            return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            viewModelScope.launch {
                chatRepository.addToken(token = token)
                userRepository.addTokenToNeo4j(token)
            }
        }
    }

    fun onNewMessage() {
    }
}

