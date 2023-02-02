package com.salazar.cheers.domain.usecase.get_notification_counter

import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNotificationCounter @Inject constructor(
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
    private val repository: FriendshipRepository,
) {
    suspend operator fun invoke(): Flow<Int> = withContext(dispatcher) {
        return@withContext repository.getFriendRequestCount()
    }
}