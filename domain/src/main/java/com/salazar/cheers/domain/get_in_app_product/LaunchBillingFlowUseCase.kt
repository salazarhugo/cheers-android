package com.salazar.cheers.domain.get_in_app_product

import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LaunchBillingFlowUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val repository: BillingRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        activity: android.app.Activity,
        productDetails: ProductDetails,
        offerToken: String,
    ): Int = withContext(dispatcher) {
        val userID = getAccountUseCase().first()?.id ?: return@withContext -1

        return@withContext repository.launchBillingFlow(
            activity = activity,
            userID = userID,
            productDetailsParam = productDetails,
            offerToken = offerToken,
        )
    }
}
