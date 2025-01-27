package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val userRepositoryImpl: UserRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<Unit> = withContext(ioDispatcher) {
//        val emailValidator = ValidateEmail().invoke(email)
//        val passwordValidator = ValidatePassword().invoke(password)
//
//        if (!emailValidator.successful)
//            return@withContext Result.failure(Exception(emailValidator.errorMessage))
//
//        if (!passwordValidator.successful)
//            return@withContext Result.failure(Exception(passwordValidator.errorMessage))

        return@withContext try {
            val authResult = authRepository.signInWithEmailAndPassword(email, password)
                ?: return@withContext Result.failure(Exception("Error signing in with email and password"))
            val idToken = authResult.user?.getIdToken(false)?.await()
            userRepositoryImpl.getUserSignIn(authResult.user?.uid ?: "")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
