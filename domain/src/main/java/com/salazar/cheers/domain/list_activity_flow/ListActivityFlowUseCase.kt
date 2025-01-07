package com.salazar.cheers.domain.list_activity_flow

import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListActivityFlowUseCase  @Inject constructor(
    private val repository: ActivityRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        val activitiesFlow = repository.listActivityFlow()
        val filtersFlow = repository.filtersFlow

        return@withContext combine(activitiesFlow, filtersFlow) { activities, filters ->
            filterActitities(
                activities = activities,
                filter = filters,
            )
        }
    }
}

private fun filterActitities(
    activities: List<Activity>,
    filter: ActivityType,
): List<Activity> {
    if (filter == ActivityType.NONE) return activities

    return activities.filter { it.type == filter }
}
