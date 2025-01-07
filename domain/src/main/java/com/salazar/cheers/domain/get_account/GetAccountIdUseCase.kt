package com.salazar.cheers.domain.get_account

import com.salazar.cheers.core.model.UserID
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAccountIdUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Flow<UserID> {
        return withContext(dispatcher) {
            return@withContext accountRepository.getAccountFlow().mapNotNull { it?.id }
        }
    }
}