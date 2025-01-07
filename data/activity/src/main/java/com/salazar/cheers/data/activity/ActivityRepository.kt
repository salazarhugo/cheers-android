package com.salazar.cheers.data.activity

import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Interface for the Activity data layer.
 */
interface ActivityRepository {
    val filtersFlow: MutableStateFlow<ActivityType>

    fun updateFilter(filter: ActivityType): Unit

    /**
     * List activity of current user.
     */
    fun listActivityFlow(): Flow<List<Activity>>

    suspend fun listActivity(): Result<Unit, DataError>

    suspend fun countActivity(): Flow<Int>

    suspend fun acknowledgeAll()
}