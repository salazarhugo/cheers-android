package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        accountRepository.deleteAccount()
        authRepository.signOut()
    }
}
