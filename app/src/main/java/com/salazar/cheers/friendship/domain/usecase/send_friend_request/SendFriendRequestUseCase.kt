package com.salazar.cheers.friendship.domain.usecase.send_friend_request

import com.salazar.common.di.IODispatcher
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendFriendRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repository: FriendshipRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(userId: String) = withContext(dispatcher) {
//        val otherUser = userRepository.getUserFlow(userId).first()
//        userRepository.updateLocalUser(
//            otherUser.copy(requested = true)
//        )
        return@withContext repository.createFriendRequest(userId = userId)
    }
}