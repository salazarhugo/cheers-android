package com.salazar.cheers.feature.map.domain.usecase.update_location

import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.get_last_known_location.GetLastKnownLocationUseCase
import com.salazar.cheers.feature.map.data.repository.MapRepositoryImpl
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val mapRepository: MapRepositoryImpl,
    private val dataStoreRepository: DataStoreRepository,
    private val lastKnownLocationUseCase: GetLastKnownLocationUseCase,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        val preferences = dataStoreRepository.userPreferencesFlow.firstOrNull() ?: return@withContext

        if (preferences.ghostMode)
            return@withContext

        val lastKnownLocation = lastKnownLocationUseCase() ?: return@withContext

        mapRepository.updateLocation(
            longitude = lastKnownLocation.longitude,
            latitude = lastKnownLocation.latitude,
        )
    }
}