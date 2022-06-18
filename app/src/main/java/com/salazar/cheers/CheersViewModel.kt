package com.salazar.cheers

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.mapbox.maps.extension.style.expressions.dsl.generated.not
import com.salazar.cheers.data.StoreUserEmail
import com.salazar.cheers.data.datastore.DataStoreRepository
import com.salazar.cheers.data.entities.Theme
import com.salazar.cheers.data.entities.UserPreference
import com.salazar.cheers.data.repository.BillingRepository
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheersUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
    val user: User? = null,
    val userPreference: UserPreference = UserPreference(id = "", theme = Theme.SYSTEM),
    val notificationCount: Int = 0,
)

@HiltViewModel
class CheersViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billingRepository: BillingRepository,
    private val chatRepository: ChatRepository,
    private val storeUserEmail: StoreUserEmail,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CheersUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun queryPurchases() {
        viewModelScope.launch {
            billingRepository.queryPurchases()
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
        getAndSaveRegistrationToken()

        viewModelScope.launch {
            val user = userRepository.getCurrentUserNullable() ?: return@launch
            viewModelState.update {
                it.copy(user = user, isLoading = false)
            }
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
                FirestoreUtil.addFCMRegistrationToken(token = token)
            }
        }
    }

    fun onNewMessage() {
    }
}

