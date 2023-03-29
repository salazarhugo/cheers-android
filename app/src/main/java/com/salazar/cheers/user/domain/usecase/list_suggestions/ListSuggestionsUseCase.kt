package com.salazar.cheers.user.domain.usecase.list_suggestions

import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListSuggestionsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        return@withContext userRepository.listSuggestions()
    }
}
