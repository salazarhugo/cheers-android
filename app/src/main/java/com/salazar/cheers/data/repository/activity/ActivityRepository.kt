package com.salazar.cheers.data.repository.activity

import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.core.data.internal.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Activity data layer.
 */
interface ActivityRepository {

    /**
     * List activity of current user.
     */
    suspend fun listActivity(): Flow<Resource<List<Activity>>>

    suspend fun countActivity(): Flow<Int>

    suspend fun acknowledgeAll()
}