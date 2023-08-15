package com.salazar.cheers.domain.get_account

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        return@withContext accountRepository.getAccountFlow().map {
            it ?: throw IllegalStateException("no account")
        }
    }
}