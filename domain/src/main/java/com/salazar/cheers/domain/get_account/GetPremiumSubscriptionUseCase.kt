package com.salazar.cheers.domain.get_account

import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPremiumSubscriptionUseCase @Inject constructor(
    private val billingRepository: BillingRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Unit {
        return withContext(dispatcher) {
            return@withContext billingRepository.listSubscriptions()
        }
    }
}