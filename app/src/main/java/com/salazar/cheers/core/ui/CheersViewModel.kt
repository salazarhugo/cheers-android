package com.salazar.cheers.core.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.Settings
import com.salazar.cheers.core.model.Theme
import com.salazar.cheers.core.model.UserPreference
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.chat.websocket.ChatWebSocketManager
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.update_id_token.UpdateIdTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface CheersUiState {
    data object Loading: CheersUiState

    data class Initialized(
        val account: Account?,
        val isLoggedIn: Boolean,
        val userPreference: UserPreference = UserPreference(id = "", theme = Theme.SYSTEM),
        val unreadChatCount: Int = 0,
        val settings: Settings,
    ): CheersUiState
}

private data class CheersViewModelState(
    val account: Account? = null,
    val isLoggedIn: Boolean? = null,
    val userPreference: UserPreference = UserPreference(id = "", theme = Theme.SYSTEM),
    val unreadChatCount: Int = 0,
    val settings: Settings? = null,
) {
    fun toUiState(): CheersUiState =
        if (isLoggedIn != null && settings != null) {
            CheersUiState.Initialized(
                account = account,
                isLoggedIn = isLoggedIn,
                userPreference  = UserPreference(id = "", theme = Theme.SYSTEM),
                unreadChatCount  = 0,
                settings = settings,
            )
        } else {
            CheersUiState.Loading
        }
}

@HiltViewModel
class CheersViewModel @Inject constructor(
    private val webSocketManager: ChatWebSocketManager,
    private val userRepositoryImpl: UserRepositoryImpl,
    private val accountRepository: AccountRepository,
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val updateIdTokenUseCase: UpdateIdTokenUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        CheersViewModelState()
    )

    val uiState = viewModelState
        .map(CheersViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            accountRepository.getAccountFlow().collect(::updateAccount)
        }

        viewModelScope.launch {
            dataStoreRepository.userPreferencesFlow.collect(::updateSettings)
        }

        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.getUnreadChatCount().collect { unreadChatCount ->
                viewModelState.update {
                    it.copy(unreadChatCount = unreadChatCount)
                }
            }
        }

        initWebsocket()
    }

    private fun initWebsocket() {
        viewModelScope.launch {
            webSocketManager.connect()
        }
    }

    fun onAuthChange(auth: FirebaseAuth) {
        try {
            Log.i("AUTH1", auth.currentUser?.uid.toString())

            if (auth.currentUser == null) {
                viewModelState.update {
                    it.copy(isLoggedIn = false)
                }
                return
            }

            viewModelScope.launch(Dispatchers.IO) {
                val idTokenResult = auth.currentUser?.getIdToken(false)?.await()
                val idToken = idTokenResult?.token
                if (idToken != null) {
                    updateIdTokenUseCase(idToken)
                }
            }

        } catch (e: Exception) {
           e.printStackTrace()
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

        initWebsocket()
    }


    private fun updateAccount(account: Account?) {
        viewModelState.update {
            it.copy(account = account, isLoggedIn = account != null)
        }
    }
    private fun updateSettings(settings: Settings?) {
        viewModelState.update {
            it.copy(settings = settings)
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
                userRepositoryImpl.addTokenToNeo4j(token)
            }
        }
    }

    fun onNewMessage() {
    }
}

