package com.salazar.cheers.domain.usecase.remove_friend

import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveFriendUseCase @Inject constructor(
    private val repository: FriendshipRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(userId: String) = withContext(dispatcher) {
        return@withContext repository.removeFriend(userId = userId)
    }
}