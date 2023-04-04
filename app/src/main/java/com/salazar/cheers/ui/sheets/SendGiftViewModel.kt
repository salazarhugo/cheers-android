package com.salazar.cheers.ui.sheets

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.core.data.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SendGiftUiState(
    val isLoading: Boolean,
    val isConfirmationScreen: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean? = null,
    val receiver: User? = null,
    val selectedSticker: Sticker? = null,
)

@HiltViewModel
class SendGiftViewModel @Inject constructor(
    userRepository: UserRepository,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SendGiftUiState(isLoading = false))
    private lateinit var receiverId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("receiverId")?.let {
            receiverId = it
        }

        viewModelScope.launch {
            userRepository.getUserFlow(userIdOrUsername = receiverId).collect { receiver ->
                viewModelState.update {
                    it.copy(receiver = receiver)
                }
            }
        }
    }

    fun selectSticker(sticker: Sticker) {
        viewModelState.update {
            it.copy(selectedSticker = sticker)
        }
        updateConfirmation(true)
    }

    private fun updateConfirmation(isConfirmationScreen: Boolean) {
        viewModelState.update {
            it.copy(isConfirmationScreen = isConfirmationScreen)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun sendGift() {
        updateIsLoading(true)
//        FirestoreUtil.sendGift(
//            receiverId = receiverId,
//            price = uiState.value.selectedSticker?.price ?: 50
//        ).addOnSuccessListener { result ->
//            Log.e("SendGift", result.toString())
//
//            val success = result["success"] as Boolean
//
//            if (success)
//                viewModelState.update {
//                    it.copy(success = success, isLoading = false)
//                }
//            else
//                viewModelState.update {
//                    it.copy(
//                        errorMessage = result["displayError"] as String,
//                        success = success,
//                        isLoading = false
//                    )
//                }
//        }.addOnFailureListener {
//            Log.e("SendGift", it.toString())
//        }
    }
}