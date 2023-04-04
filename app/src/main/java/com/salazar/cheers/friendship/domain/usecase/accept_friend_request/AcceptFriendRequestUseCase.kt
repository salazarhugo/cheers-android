package com.salazar.cheers.friendship.domain.usecase.accept_friend_request

import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.core.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AcceptFriendRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repository: FriendshipRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(userId: String) = withContext(dispatcher) {
        val otherUser = userRepository.getUserFlow(userId).first()
        userRepository.updateLocalUser(
            otherUser.copy(requested = false, friend = true)
        )

        return@withContext repository.acceptFriendRequest(userId = userId)
    }
}