package com.salazar.cheers.friendship.domain.usecase

import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFriendRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Flow<List<UserItem>> = withContext(ioDispatcher) {
        friendshipRepository.listFriendRequest()
            .combine(userRepository.listUserItems()) { friendRequests, users ->
                friendRequests.mapNotNull { friendRequest ->
                    val user = users.find { it.id == friendRequest.id }
                    user
                }
            }
    }
}
