package com.salazar.cheers.domain.get_coins_balance

import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCoinsBalanceFlowUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(): Flow<Int> = withContext(dispatcher) {
        return@withContext dataStoreRepository.getCoinsBalance()
    }
}