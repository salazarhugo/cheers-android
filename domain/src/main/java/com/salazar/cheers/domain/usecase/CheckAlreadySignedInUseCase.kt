package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.isConnected
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckAlreadySignedInUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(): Boolean = withContext(ioDispatcher) {
        return@withContext authRepository.checkIfAlreadySignedIn() &&
                accountRepository.isConnected()
    }
}
