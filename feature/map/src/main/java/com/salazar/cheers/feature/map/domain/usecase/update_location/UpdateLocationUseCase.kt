package com.salazar.cheers.feature.map.domain.usecase.update_location

import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.feature.map.data.repository.MapRepositoryImpl
import com.salazar.cheers.feature.map.location.DefaultLocationClient
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val mapRepository: MapRepositoryImpl,
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