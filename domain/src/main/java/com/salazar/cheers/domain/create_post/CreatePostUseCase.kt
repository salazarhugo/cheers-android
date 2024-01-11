package com.salazar.cheers.domain.create_post

import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(
        vararg pairs: Pair<String, Any?>
    ) = withContext(dispatcher) {

        return@withContext postRepository.uploadPost(*pairs)
    }
}