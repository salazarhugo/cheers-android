package com.salazar.cheers.domain.list_post

import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ListPostUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val repository: PostRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        userId: String? = null
    ): Flow<List<Post>> {
        return withContext(dispatcher) {
            val uid = userId ?: getAccountUseCase().first()?.id ?: return@withContext flowOf(emptyList())
            return@withContext repository.listPost(userIdOrUsername = uid)
        }
    }
}