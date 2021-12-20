package com.salazar.cheers.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.util.Neo4jUtil
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
class ChooseUsernameViewModel @Inject constructor() : ViewModel() {

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

    fun isUsernameAvailable() {
        val username = uiState.value.username
        // Ui state is refreshing
        uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = Neo4jUtil.isUsernameAvailable(username)
            uiState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        isAvailable = result.data,
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