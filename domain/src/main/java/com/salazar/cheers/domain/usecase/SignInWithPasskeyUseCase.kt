package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.toAccount
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.shared.data.response.UserResponse
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithPasskeyUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val signInUseCase: SignInCheersUseCase,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        user: UserResponse,
        customToken: String,
    ): Result<Unit> = withContext(ioDispatcher) {
        val authResult = authRepository.signInWithCustomToken(token = customToken).getOrNull()
            ?: return@withContext Result.failure(Exception("Failed to sign in firebase with custom token"))

        accountRepository.putAccount(
            account = user.toAccount(),
        )

        return@withContext signInUseCase()
            .fold(
                onSuccess = { Result.success(Unit) },
                onFailure = { Result.failure(it) }
            )
    }
}
