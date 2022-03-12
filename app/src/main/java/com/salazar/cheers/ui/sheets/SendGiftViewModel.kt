package com.salazar.cheers.ui.sheets

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SendGiftUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val success: Boolean? = null,
    val receiver: User? = null,
)

class SendGiftViewModel @AssistedInject constructor(
    application: Application,
    userRepository: UserRepository,
    @Assisted private val receiverId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SendGiftUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            val receiver = userRepository.getUser(userId = receiverId)
            viewModelState.update {
                it.copy(receiver = receiver)
            }
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun sendGift() {
        updateIsLoading(true)
        FirestoreUtil.sendGift(receiverId = receiverId).addOnSuccessListener { result ->
            Log.e("SendGift", result.toString())

            val success = result["success"] as Boolean

            if (success)
                viewModelState.update {
                    it.copy(success = success, isLoading = false)
                }
            else
                viewModelState.update {
                    it.copy(
                        errorMessage = result["displayError"] as String,
                        success = success,
                        isLoading = false
                    )
                }
        }.addOnFailureListener {
            Log.e("SendGift", it.toString())
        }
    }

    @AssistedFactory
    interface SendGiftViewModelFactory {
        fun create(receiverId: String): SendGiftViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: SendGiftViewModelFactory,
            receiverId: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(receiverId = receiverId) as T
            }
        }
    }
}

@Composable
fun sendGiftViewModel(receiverId: String): SendGiftViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).sendGiftViewModelFactory()

    return viewModel(factory = SendGiftViewModel.provideFactory(factory, receiverId = receiverId))
}