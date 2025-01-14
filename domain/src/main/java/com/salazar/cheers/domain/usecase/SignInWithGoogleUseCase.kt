package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.domain.update_id_token.UpdateIdTokenUseCase
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val signInUseCase: SignInCheersUseCase,
    private val updateIdTokenUseCase: UpdateIdTokenUseCase,
) {
    suspend operator fun invoke(
        idToken: String,
    ): Result<Boolean> = withContext(ioDispatcher) {
        return@withContext try {
            val authResult = authRepository.signInWithFirebaseIdToken(idToken = idToken)
                ?: return@withContext Result.failure(Exception("Error signing in firebase with id token"))

            val token = authResult.user?.getIdToken(true)?.await()
                ?: return@withContext Result.failure(Exception("failed to refresh id token"))

            updateIdTokenUseCase(token.token.orEmpty())

            return@withContext signInUseCase()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
