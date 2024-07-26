package com.salazar.cheers.domain.get_in_app_product

import com.salazar.cheers.core.model.ProductDetails
import com.salazar.cheers.data.billing.BillingRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListInAppProductUseCase @Inject constructor(
    private val repository: BillingRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
    ): Result<List<ProductDetails>> = withContext(dispatcher) {
        return@withContext repository.queryProductDetails()
    }
}
