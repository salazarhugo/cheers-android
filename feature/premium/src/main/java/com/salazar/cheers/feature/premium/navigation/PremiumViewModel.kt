package com.salazar.cheers.feature.premium.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.VideoChat
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.core.model.SubscriptionOfferDetails
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.domain.get_account.GetIsPremiumUseCase
import com.salazar.cheers.domain.get_in_app_product.GetInAppProductUseCase
import com.salazar.cheers.domain.get_in_app_product.LaunchBillingFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PremiumFeature(
    val name: String,
    val description: String,
    val icon: ImageVector? = null,
)

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val getInAppProductUseCase: GetInAppProductUseCase,
    private val billingRepository: BillingRepository,
    private val launchBillingFlowUseCase: LaunchBillingFlowUseCase,
    private val getIsPremiumUseCase: GetIsPremiumUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PremiumUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        val features = listOf(
            PremiumFeature(
                name = "Exclusive Parties",
                description = "Get access to exclusive parties reserved for premium users.",
                icon = Icons.Outlined.Stars,
            ),
            PremiumFeature(
                name = "No Ads",
                description = "No more ads in your feed where Cheers sometimes shows ads.",
                icon = Icons.Default.NotInterested,
            ),
            PremiumFeature(
                name = "Premium Badge",
                description = "Get an exclusive badge next to your name showing your VIP status within the Cheers community.",
                icon = Icons.Outlined.WorkspacePremium,
            ),
            PremiumFeature(
                name = "Special Discounts & Offers",
                description = "Enjoy exclusive deals from our partners.",
                icon = Icons.Outlined.Discount,
            ),
            PremiumFeature(
                name = "Early Access to New Features",
                description = "Be the first to try the latest and greatest features.",
                icon = Icons.Outlined.AccessTime,
            ),
            PremiumFeature(
                name = "Priority Support",
                description = "Get help quickly and efficiently from our dedicated support team.",
                icon = Icons.Outlined.SupportAgent,
            ),
            PremiumFeature(
                name = "Exclusive Chat Features",
                description = "Enjoy high-quality video calls, ad-free messaging, and more.",
                icon = Icons.Outlined.VideoChat,
            ),
        )
        viewModelState.update { it.copy(features = features) }

        billingRepository.startConnection()

        listenToPurchasedProducts()

        viewModelScope.launch {
            val isPremium = getIsPremiumUseCase()
            viewModelState.update { it.copy(isPremium = isPremium, isLoading = false) }
        }

        viewModelScope.launch {
            val subscriptionProduct =
                getInAppProductUseCase(productId = "cheers_premium").getOrNull()
            viewModelState.update { it.copy(subscriptionProductID = subscriptionProduct?.id) }
            val plans = subscriptionProduct?.offers ?: return@launch

            updatePlans(plans)
        }
    }

    private fun listenToPurchasedProducts() {
        viewModelScope.launch {
            billingRepository.purchaseUpdateFlowData.collect {
                if (it.isNullOrEmpty()) return@collect

                updateSuccess(true)
            }
        }
    }

    fun updateSuccess(success: Boolean) {
        viewModelState.update { it.copy(success = success) }
    }

    private fun updatePlans(offers: List<SubscriptionOfferDetails>) {
        viewModelState.update { it.copy(plans = offers, selectedPlan = offers.firstOrNull()) }
    }


    fun onPlanClick(plan: SubscriptionOfferDetails) {
        viewModelState.update { it.copy(selectedPlan = plan) }
    }

    fun onSubscribeClick(
        activity: android.app.Activity,
    ) {
        val productDetails = ProductDetails(
            id = "cheers_premium",
            type = BillingClient.ProductType.SUBS,
        )
        val offerToken = uiState.value.selectedPlan?.offerToken ?: return

        viewModelScope.launch {
            val responseCode = launchBillingFlowUseCase(
                activity = activity,
                productDetails = productDetails,
                offerToken = offerToken,
            )
            when (responseCode) {
                BillingClient.BillingResponseCode.OK -> Unit
                BillingClient.BillingResponseCode.USER_CANCELED -> Unit
                else -> Unit
            }
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
}