package com.salazar.cheers.domain.list_suggestions

import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListSuggestionsUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() = withContext(ioDispatcher) {
        return@withContext userRepositoryImpl.listSuggestions()
    }
}
