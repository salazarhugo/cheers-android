package com.salazar.cheers.map.domain.usecase.update_location

import com.salazar.cheers.core.data.datastore.DataStoreRepository
import com.salazar.cheers.core.data.location.DefaultLocationClient
import com.salazar.cheers.core.di.IODispatcher
import com.salazar.cheers.map.data.repository.MapRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val locationClient: DefaultLocationClient,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        val preferences = dataStoreRepository.userPreferencesFlow.firstOrNull() ?: return@withContext

        if (preferences.ghostMode)
            return@withContext

        val lastKnownLocation = locationClient.getLastKnownLocation() ?: return@withContext

        mapRepository.updateLocation(
            longitude = lastKnownLocation.longitude,
            latitude = lastKnownLocation.latitude,
        )
    }
}