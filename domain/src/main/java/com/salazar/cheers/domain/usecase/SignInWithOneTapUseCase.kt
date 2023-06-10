package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import com.salazar.common.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithOneTapUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        idToken: String?,
    ): Resource<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val authResult = authRepository.signInWithOneTap(idToken = idToken)
                ?: return@withContext Resource.Error("Error signing in with one tap")
            userRepository.getUserSignIn(authResult.user?.uid ?: "")
            Resource.Success(null)
        } catch (e: java.lang.Exception) {
            Resource.Error("Error signing in with one tap")
        }
    }
}
