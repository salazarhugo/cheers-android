package com.salazar.cheers.post.domain.usecase.feed_post

import com.salazar.cheers.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ListPostFeedUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repository: com.salazar.cheers.data.post.repository.PostRepository,
) {
    operator fun invoke(): Flow<List<com.salazar.cheers.data.post.repository.Post>> {
        return repository.getPostFeedFlow()
            .combine(userRepository.listUserItems()) { posts, users ->
                // Only friend posts
                posts.filter { post ->
                    post.isAuthor || users.find { it.id == post.authorId }?.friend == true
                }
            }
    }
}