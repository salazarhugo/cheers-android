package com.salazar.cheers.data.repository.account

import cheers.account.v1.Account
import com.salazar.cheers.data.Resource
import com.salazar.cheers.internal.Activity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Activity data layer.
 */
interface AccountRepository {

    /**
     * Get coins of current user.
     */
    suspend fun getAccount(): Result<Account>
}