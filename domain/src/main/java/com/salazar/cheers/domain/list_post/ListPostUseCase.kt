package com.salazar.cheers.domain.list_post

import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ListPostUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val repository: PostRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        page: Int,
        userId: String? = null
    ): Result<Unit, DataError> {
        return withContext(dispatcher) {
            val uid = userId ?: getAccountUseCase().first()?.id ?: return@withContext Result.Error(
                DataError.Network.UNKNOWN
            )
            return@withContext repository.listPost(
                page = page,
                userIdOrUsername = uid
            )
        }
    }
}