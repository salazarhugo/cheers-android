package com.salazar.cheers.domain.get_id_token

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.isNotConnected
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.common.di.IODispatcher
import com.salazar.common.util.result.DataError
import com.salazar.common.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetIdTokenUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Result<String, DataError> = withContext(dispatcher) {
        if (accountRepository.isNotConnected()) {
            return@withContext Result.Error(DataError.Auth.NOT_SIGNED_IN)
        }

        val idToken = accountRepository.getAccountFlow().map {
            it?.idToken
        }.firstOrNull() ?: return@withContext Result.Error(DataError.Network.UNKNOWN)

        return@withContext Result.Success(idToken)
    }
}