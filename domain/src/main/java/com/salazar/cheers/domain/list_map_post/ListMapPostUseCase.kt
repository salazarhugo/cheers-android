package com.salazar.cheers.domain.list_map_post

import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.Post
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ListMapPostUseCase @Inject constructor(
    private val repository: PostRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Flow<List<Post>> {
        return withContext(dispatcher) {
            return@withContext repository.getMapPostFlow(privacy = Privacy.FRIENDS)
        }
    }
}