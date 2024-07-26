package com.salazar.cheers.domain.get_location_updates

import com.salazar.cheers.shared.data.location.DefaultLocationClient
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLocationUpdatesUseCase @Inject constructor(
    private val locationClient: DefaultLocationClient,
    @MainDispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(interval: Long) = withContext(dispatcher) {
        return@withContext locationClient.getLocationUpdates(interval = interval)
    }
}