package com.salazar.cheers.domain.get_account

import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetIsPremiumUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Boolean {
        return withContext(dispatcher) {
            return@withContext userRepository.getCurrentUser().premium
        }
    }
}