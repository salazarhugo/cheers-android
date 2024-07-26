package com.salazar.cheers.domain.list_post_likes

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListPostLikesUseCase @Inject constructor(
    private val repository: PostRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        postID: String
    ): Flow<List<UserItem>> {
        return withContext(dispatcher) {
            return@withContext repository.listPostLikes(postID = postID)
        }
    }
}