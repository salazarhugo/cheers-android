package com.salazar.cheers.ui.compose.bottombar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountBottomSheetUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val room: ChatChannel? = null,
    val account: Account? = null,
)

@HiltViewModel
class AccountBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val signOutUseCase: SignOutUseCase,
    private val getAccountUseCase: GetAccountUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(AccountBottomSheetUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            getAccountUseCase().collect(::updateAccount)
        }
    }

    fun updateAccount(account: Account?) {
        viewModelState.update {
            it.copy(account= account)
        }
    }

    fun onSignOut(onFinished: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            onFinished()
        }
    }
}
