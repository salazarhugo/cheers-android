package com.salazar.cheers.post.domain.usecase.feed_post

import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.post.data.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ListPostFeedUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repository: PostRepository,
) {
    operator fun invoke(): Flow<List<Post>> {
        return repository.getPostFeedFlow()
            .combine(userRepository.listUserItems()) { posts, users ->
                // Only friend posts
                posts.filter { post ->
                    post.isAuthor || users.find { it.id == post.authorId }?.friend == true
                }
            }
    }
}