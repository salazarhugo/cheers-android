package com.salazar.cheers.feature.settings.recharge

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.data.user.account.AccountRepository
import com.salazar.cheers.domain.get_in_app_product.LaunchBillingFlowUseCase
import com.salazar.cheers.domain.get_in_app_product.ListInAppProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val billingRepository: BillingRepository,
    private val listInAppProductUseCase: ListInAppProductUseCase,
    private val launchBillingFlowUseCase: LaunchBillingFlowUseCase,
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
            accountRepository.getAccount().onSuccess {
                updateCoins(it.coins)
            }
        }
        billingRepository.startConnection()
        refreshSkuDetails()
    }


    private fun updateCoins(coins: Int) {
        viewModelState.update {
            it.copy(coins = coins)
        }
    }

    fun refreshSkuDetails() {
        viewModelScope.launch {
            listInAppProductUseCase()
                .onSuccess { productDetails ->
                    viewModelState.update {
                        it.copy(productDetails = productDetails.sortedBy { it.formattedPrice })
                    }
                }.onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun onProductClick(
        productDetails: ProductDetails,
        activity: Activity
    ) {
        viewModelScope.launch {
            launchBillingFlowUseCase(activity, productDetails, "")
        }
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
    val productDetails: List<ProductDetails>? = null,
)

