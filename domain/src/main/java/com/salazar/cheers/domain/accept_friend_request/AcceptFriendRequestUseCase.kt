package com.salazar.cheers.domain.accept_friend_request

import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AcceptFriendRequestUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val repository: FriendshipRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(userId: String) = withContext(dispatcher) {
        val otherUser = userRepositoryImpl.getUserFlow(userId).first()
        userRepositoryImpl.updateLocalUser(
            otherUser.copy(requested = false, friend = true)
        )

        return@withContext repository.acceptFriendRequest(userId = userId)
    }
}