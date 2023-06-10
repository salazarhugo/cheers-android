package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckAlreadySignedInUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Boolean = withContext(ioDispatcher) {
        val uid = authRepository.checkIfAlreadySignedIn() ?: return@withContext false
        val user = userRepository.getUserFlow(uid).firstOrNull() ?: return@withContext false
        return@withContext true
    }
}
