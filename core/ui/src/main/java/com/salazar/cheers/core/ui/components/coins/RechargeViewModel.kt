package com.salazar.cheers.core.ui.components.coins

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.domain.get_coins_balance.GetCoinsBalanceFlowUseCase
import com.salazar.cheers.domain.get_coins_balance.GetCoinsBalanceUseCase
import com.salazar.cheers.domain.get_in_app_product.GetPurchaseProductState
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
class RechargeCoinsViewModel @Inject constructor(
    private val getCoinsBalanceFlowUseCase: GetCoinsBalanceFlowUseCase,
    private val getCoinsBalanceUseCase: GetCoinsBalanceUseCase,
    private val listInAppProductUseCase: ListInAppProductUseCase,
    private val launchBillingFlowUseCase: LaunchBillingFlowUseCase,
    private val getPurchaseProductState: GetPurchaseProductState,
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
            getCoinsBalanceFlowUseCase()
                .collect(::updateCoins)
        }
        viewModelScope.launch {
            getCoinsBalanceUseCase()
        }
        refreshSkuDetails()
        listenToPurchasedProducts()
    }


    private fun listenToPurchasedProducts() {
        viewModelScope.launch {
            getPurchaseProductState().collect {
                if (it.isNullOrEmpty()) return@collect

                viewModelScope.launch {
                    getCoinsBalanceUseCase()
                }
            }
        }
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

