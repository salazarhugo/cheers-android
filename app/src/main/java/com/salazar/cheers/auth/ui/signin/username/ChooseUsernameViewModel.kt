package com.salazar.cheers.auth.ui.signin.username
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

    fun clearUsername() {
        uiState.update {
            it.copy(username = "")
        }
    }

    fun onUsernameChanged(username: String) {
        uiState.update {
            it.copy(username = username)
        }
    }

    fun reset() {
        uiState.update {
            ChooseUsernameState()
        }
    }

    fun updateErrorMessage(error: String) {
        uiState.update {
            it.copy(errorMessage = error)
        }
    }

    private fun isLowerCase(username: String): Boolean {
        val isLower = username == username.lowercase()
        if (!isLower)
            updateErrorMessage("Must be lowercase")
        return isLower
    }

    private fun hasValidChars(username: String): Boolean {
        val regex = Regex("^[._a-z0-9]+\$")
        val validChars = username.matches(regex)
        if (!validChars)
            updateErrorMessage("Only dots and underscores are allowed")
        return validChars
    }

    private fun validateUsername(username: String): Boolean {
        val regex = Regex("^(?!.*\\.\\.)(?!.*\\.\$)[^\\W][\\w.]{0,29}\$")
        return isLowerCase(username) && hasValidChars(username) && username.matches(regex)
    }

}