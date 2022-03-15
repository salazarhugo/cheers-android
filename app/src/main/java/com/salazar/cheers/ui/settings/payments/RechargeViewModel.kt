package com.salazar.cheers.ui.settings.payments

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.SkuDetails
import com.salazar.cheers.data.repository.BillingRepository
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RechargeUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            FirestoreUtil.getUserCoins().collect { coins ->
                viewModelState.update {
                    it.copy(coins = coins)
                }
            }
        }
        billingRepository.startConnection()
        refreshSkuDetails()
    }

    fun refreshSkuDetails() {
        viewModelScope.launch {
            val skuDetails = billingRepository.querySkuDetails().skuDetailsList
            if (skuDetails != null) {
                viewModelState.update {
                    it.copy(skuDetails = skuDetails.sortedBy { it.priceAmountMicros })
                }
            }
        }
    }

    fun onSkuClick(
        skuDetails: SkuDetails,
        activity: Activity
    ) {
        billingRepository.launchBillingFlow(skuDetails = skuDetails, activity = activity)
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

}

data class RechargeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val coins: Int = 0,
    val skuDetails: List<SkuDetails>? = null,
)

