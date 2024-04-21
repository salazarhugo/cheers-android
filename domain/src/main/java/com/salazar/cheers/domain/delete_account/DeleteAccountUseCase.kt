package com.salazar.cheers.domain.delete_account

import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.domain.usecase.SignOutUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val signOutUseCase: SignOutUseCase,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Result<Unit> {
        return withContext(dispatcher) {
            return@withContext authRepository.deleteAccount().onSuccess {
                signOutUseCase()
            }
        }
    }
}