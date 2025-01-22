package com.salazar.cheers.domain.get_in_app_product

import com.android.billingclient.api.Purchase
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPurchaseProductState @Inject constructor(
    private val repository: BillingRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Flow<List<Purchase>?> = withContext(dispatcher) {
        return@withContext repository.purchaseUpdateFlowData
    }
}
