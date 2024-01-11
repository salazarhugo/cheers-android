package com.salazar.cheers.domain.send_friend_request

import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendFriendRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repository: FriendshipRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(userId: String) = withContext(dispatcher) {
        val otherUser = userRepository.getUserFlow(userId).firstOrNull()
        if (otherUser != null) {
            userRepository.updateLocalUser(
                otherUser.copy(requested = true)
            )
        }
        return@withContext repository.createFriendRequest(userId = userId)
    }
}