package com.salazar.cheers.domain.update_id_token

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
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

        accountRepository.putIdToken(idToken = idToken)

        return@withContext Result.success(idToken)
    }
}