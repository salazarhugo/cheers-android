package com.salazar.cheers.core.domain.usecase.get_notification_counter

import com.salazar.cheers.data.repository.activity.ActivityRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.core.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNotificationCounterUseCase @Inject constructor(
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
    private val repository: FriendshipRepository,
    private val activityRepository: ActivityRepository,
) {
    suspend operator fun invoke(): Flow<Int> = withContext(dispatcher) {
        activityRepository.countActivity().combine(repository.getFriendRequestCount()) { a, b ->
            return@combine a+b
        }
    }
}