package com.salazar.cheers.domain.get_id_token

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.isNotConnected
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetIdTokenUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Result<String> = withContext(dispatcher) {
        if (accountRepository.isNotConnected()) {
            return@withContext Result.failure(Exception("Not connected"))
        }

        val idToken = accountRepository.getAccountFlow().map {
            it?.idToken
        }.firstOrNull() ?: return@withContext Result.failure(Exception("failed to get idtoken"))

        return@withContext Result.success(idToken)
    }
}