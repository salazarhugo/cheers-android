package com.salazar.cheers.data.activity

import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.shared.util.Resource
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