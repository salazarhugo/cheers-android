package com.salazar.cheers.domain.get_account

import com.salazar.cheers.data.account.Account
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(): Flow<Account?> = withContext(dispatcher) {
        return@withContext accountRepository.getAccountFlow()
    }
}