package com.salazar.cheers.domain.delete_post

import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        postID: String,
    ) = withContext(dispatcher) {
        return@withContext repository.deletePost(postId = postID)
    }
}