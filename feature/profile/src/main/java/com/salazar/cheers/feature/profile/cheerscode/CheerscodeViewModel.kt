package com.salazar.cheers.feature.profile.cheerscode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.shared.util.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CheerscodeUiState(
    val isLoading: Boolean,
    val errorMessages: String,
    val link: String,
)


data class CheerscodeViewModelState(
    val isLoading: Boolean = false,
    val errorMessages: String = "",
    val link: String = String(),
)

@HiltViewModel
class CheerscodeViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CheerscodeViewModelState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value,
        )

    init {
        viewModelScope.launch {
            val account = getAccountUseCase().first() ?: return@launch
            val result = FirebaseDynamicLinksUtil.createShortLink("u/${account.username}")
            when (result) {
                is Result.Error -> {}
                is Result.Success -> updateLink(result.data)
            }
        }
    }

    private fun updateLink(link: String) {
        viewModelState.update {
            it.copy(link = link)
        }
    }
}