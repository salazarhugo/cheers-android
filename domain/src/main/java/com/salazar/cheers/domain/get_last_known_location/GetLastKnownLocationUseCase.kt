package com.salazar.cheers.domain.get_last_known_location

import com.salazar.cheers.shared.data.location.DefaultLocationClient
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLastKnownLocationUseCase @Inject constructor(
    private val locationClient: DefaultLocationClient,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        return@withContext locationClient.getLastKnownLocation()
    }
}