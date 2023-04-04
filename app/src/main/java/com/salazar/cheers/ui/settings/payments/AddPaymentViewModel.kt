package com.salazar.cheers.ui.settings.payments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.internal.Source
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PaymentUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val sources: List<Source> = emptyList(),
)

@HiltViewModel
class AddPaymentViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(PaymentUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
//            FirestoreUtil.listenSources().collect { sources ->
//                viewModelState.update {
//                    it.copy(sources = sources, isLoading = false)
//                }
//            }
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun addCard(
        context: Context,
    ) {
        updateIsLoading(true)
    }
}

