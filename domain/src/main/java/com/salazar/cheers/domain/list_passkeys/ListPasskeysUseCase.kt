package com.salazar.cheers.domain.list_passkeys

import com.salazar.cheers.core.model.Credential
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ListPasskeysUseCase @Inject constructor(
    private val repository: AuthRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<List<Credential>, DataError> {
        return withContext(dispatcher) {
            return@withContext repository.listPasskeys()
        }
    }
}