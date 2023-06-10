package com.salazar.cheers.domain.get_notification_counter

import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNotificationCounterUseCase @Inject constructor(
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
    private val repository: com.salazar.cheers.data.friendship.FriendshipRepository,
    private val activityRepository: ActivityRepository,
) {
    suspend operator fun invoke(): Flow<Int> = withContext(dispatcher) {
        activityRepository.countActivity().combine(repository.getFriendRequestCount()) { a, b ->
            return@combine a+b
        }
    }
}