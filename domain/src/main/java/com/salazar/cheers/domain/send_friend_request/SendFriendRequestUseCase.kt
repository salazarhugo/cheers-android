package com.salazar.cheers.domain.send_friend_request

import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendFriendRequestUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val repository: FriendshipRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(userId: String) = withContext(dispatcher) {
        val otherUser = userRepositoryImpl.getUserFlow(userId).firstOrNull()
        if (otherUser != null) {
            userRepositoryImpl.updateLocalUser(
                otherUser.copy(requested = true)
            )
        }
        return@withContext repository.createFriendRequest(userId = userId)
    }
}