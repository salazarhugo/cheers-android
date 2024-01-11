package com.salazar.cheers.feature.signup
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ChooseUsernameState(
    val isLoading: Boolean = false,
    val isAvailable: Boolean? = null,
    val errorMessage: String = "",
    val username: String = "",
)

@HiltViewModel
class ChooseUsernameViewModel @Inject constructor(
) : ViewModel() {

    val uiState = MutableStateFlow(ChooseUsernameState(isLoading = false))

    fun updateErrorMessage(error: String) {
        uiState.update {
            it.copy(errorMessage = error)
        }
    }
}