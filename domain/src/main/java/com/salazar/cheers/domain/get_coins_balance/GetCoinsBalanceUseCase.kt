package com.salazar.cheers.domain.get_coins_balance

import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCoinsBalanceUseCase @Inject constructor(
    private val accountRepository: com.salazar.cheers.data.user.account.AccountRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(): Int = withContext(dispatcher) {
        return@withContext accountRepository.getAccount().fold(
            onSuccess = { it.coins },
            onFailure = { 0 }
        )
    }
}