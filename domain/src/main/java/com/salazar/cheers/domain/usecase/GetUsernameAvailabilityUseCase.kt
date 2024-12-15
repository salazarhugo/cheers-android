package com.salazar.cheers.domain.usecase

import com.salazar.cheers.core.model.CheckUsernameResult
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUsernameAvailabilityUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepositoryImpl,
) {
    suspend operator fun invoke(
        username: String,
    ): Resource<CheckUsernameResult> = withContext(ioDispatcher) {
        return@withContext userRepository.checkUsername(username = username).fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(it.localizedMessage) }
        )
    }
}
