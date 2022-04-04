package com.salazar.cheers.ui.auth.signin.username

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChooseUsernameState(
    val isLoading: Boolean = false,
    val isAvailable: Boolean? = null,
    val errorMessage: String = "",
    val username: String = "",
)

@HiltViewModel
class ChooseUsernameViewModel @Inject constructor(
    service: Neo4jService,
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

    fun isUsernameAvailable() {
        updateErrorMessage("")
        val username = uiState.value.username
        // Ui state is refreshing
        uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = Neo4jUtil.isUsernameAvailable(username)
            if (result is Result.Success && !result.data)
                updateErrorMessage("This username is taken")
            uiState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        isAvailable = result.data && validateUsername(username = username),
                        username = username,
                        isLoading = false
                    )
                    is Result.Error -> {
                        it.copy(
                            isAvailable = false,
                            errorMessage = it.errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}