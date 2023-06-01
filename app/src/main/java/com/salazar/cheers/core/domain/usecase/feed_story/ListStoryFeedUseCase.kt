package com.salazar.cheers.core.domain.usecase.feed_story

import com.salazar.cheers.core.domain.model.UserWithStories
import com.salazar.cheers.data.repository.story.StoryRepository
import com.salazar.cheers.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListStoryFeedUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repository: StoryRepository,
) {
    suspend operator fun invoke(): Flow<List<UserWithStories>> {
        return repository.feedStory(1, 10)
            .combine(userRepository.listUserItems()) { stories, users ->
                stories
                    .groupBy { story ->
                        story.authorId
                    }
                    .map { userIdWithStories ->
                        UserWithStories(
                            user = users.find { it.id == userIdWithStories.key }
                                ?: com.salazar.cheers.core.model.UserItem(),
                            stories = userIdWithStories.value,
                        )
                    }
            }
            .map { userWithStories ->
                userWithStories
                    // Sort by un-viewed story first
                    .sortedBy { it.stories.all { it.viewed } }
            }
    }
}