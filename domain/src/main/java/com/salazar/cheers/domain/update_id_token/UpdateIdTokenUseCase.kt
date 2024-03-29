package com.salazar.cheers.domain.update_id_token

import com.salazar.cheers.data.account.Account
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.isNotConnected
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateIdTokenUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        idToken: String,
    ): Result<String> = withContext(dispatcher) {
        if (idToken.isBlank()) {
            return@withContext Result.failure(Exception("blank id token"))
        }

        val account = accountRepository.getAccount() ?: Account()

        accountRepository.putAccount(account.copy(idToken = idToken))

        return@withContext Result.success(idToken)
    }
}