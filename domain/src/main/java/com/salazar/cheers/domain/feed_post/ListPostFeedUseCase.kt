package com.salazar.cheers.domain.feed_post

import com.salazar.cheers.core.Post
import com.salazar.cheers.data.user.UserRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ListPostFeedUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val repository: com.salazar.cheers.data.post.repository.PostRepository,
) {
    operator fun invoke(): Flow<List<Post>> {
        return repository.getPostFeedFlow()
            .combine(userRepositoryImpl.listUserItems()) { posts, users ->
                // Only friend posts
//                posts.filter { post ->
//                    post.isAuthor || users.find { it.id == post.authorId }?.friend == true
//                }
                posts
            }
    }
}