package com.salazar.cheers.domain.usecase.feed_post

import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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