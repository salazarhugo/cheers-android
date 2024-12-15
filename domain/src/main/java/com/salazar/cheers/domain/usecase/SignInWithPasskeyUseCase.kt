package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.toAccount
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.domain.update_id_token.UpdateIdTokenUseCase
import com.salazar.cheers.shared.data.response.UserResponse
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithPasskeyUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val signInUseCase: SignInCheersUseCase,
    private val accountRepository: AccountRepository,
    private val updateIdTokenUseCase: UpdateIdTokenUseCase,
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

        val token = authResult.user?.getIdToken(true)?.await()
            ?: return@withContext Result.failure(Exception("failed to refresh id token"))

        updateIdTokenUseCase(idToken = token.token.orEmpty())

        return@withContext signInUseCase()
            .fold(
                onSuccess = { Result.success(Unit) },
                onFailure = { Result.failure(it) }
            )
    }
}
