package com.salazar.cheers.feature.premium.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.core.model.User
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.domain.get_in_app_product.GetInAppProductUseCase
import com.salazar.cheers.domain.get_in_app_product.LaunchBillingFlowUseCase
import com.salazar.cheers.domain.get_in_app_product.ListInAppProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val getInAppProductUseCase: GetInAppProductUseCase,
    private val billingRepository: BillingRepository,
    private val launchBillingFlowUseCase: LaunchBillingFlowUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PremiumViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        billingRepository.startConnection()
    }

    fun onSubscribeClick(
        activity: android.app.Activity,
    ) {
        val productDetails = ProductDetails(id = "cheers_premium")
        viewModelScope.launch {
            launchBillingFlowUseCase(activity, productDetails)
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user, isLoading = false)
        }
    }
}