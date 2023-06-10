package com.salazar.cheers.domain.usecase

import com.salazar.auth.validators.ValidateEmail
import com.salazar.auth.validators.ValidatePassword
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import com.salazar.common.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Resource<Unit> = withContext(ioDispatcher) {
        val emailValidator = ValidateEmail().invoke(email)
        val passwordValidator = ValidatePassword().invoke(password)

        if (!emailValidator.successful)
            return@withContext Resource.Error(emailValidator.errorMessage)

        if (!passwordValidator.successful)
            return@withContext Resource.Error(passwordValidator.errorMessage)

        return@withContext try {
            val authResult = authRepository.signInWithEmailAndPassword(email, password)
                ?: return@withContext Resource.Error("Error signing in with email and password")
            userRepository.getUserSignIn(authResult.user?.uid ?: "")
            Resource.Success(null)
        } catch (e: java.lang.Exception) {
            Resource.Error("Error signing in with email and password")
        }
    }
}
