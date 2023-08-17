package com.salazar.cheers.core.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.data.user.Theme
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserPreference
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.feature.chat.data.repository.ChatRepository
import com.salazar.cheers.feature.chat.data.websocket.ChatWebSocketListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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
    private val chatWebSocketListener: ChatWebSocketListener,
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
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.getUnreadChatCount().collect { unreadChatCount ->
                viewModelState.update {
                    it.copy(unreadChatCount = unreadChatCount)
                }
            }
        }
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

        viewModelScope.launch(Dispatchers.IO) {
            getAndSaveRegistrationToken()
        }

        viewModelScope.launch(Dispatchers.IO) {
//            initWebSocket(auth.currentUser!!)
        }

        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.getUnreadChatCount().collect { unreadChatCount ->
                viewModelState.update {
                    it.copy(unreadChatCount = unreadChatCount)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
//            chatRepository.getInbox()
        }

        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getCurrentUserFlow().collect(::updateUser)
        }
    }

    private suspend fun initWebSocket(user: FirebaseUser) = withContext(Dispatchers.IO) {
        val task: Task<GetTokenResult> = user.getIdToken(false)
        val tokenResult = task.await()
        val idToken = tokenResult.token ?: return@withContext

        val request = Request.Builder()
            .url("${Constants.WEBSOCKET_URL}?token=" + idToken)
            .build()

        val client = OkHttpClient()

        client.newWebSocket(request, chatWebSocketListener)
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user, isLoading = false)
        }
    }

    private suspend fun getAndSaveRegistrationToken() {
        if (FirebaseAuth.getInstance().currentUser == null)
            return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.addToken(token = token)
                userRepository.addTokenToNeo4j(token)
            }
        }
    }

    fun onNewMessage() {
    }
}

