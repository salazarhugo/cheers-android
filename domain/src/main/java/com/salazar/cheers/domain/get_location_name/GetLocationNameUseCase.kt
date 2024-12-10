package com.salazar.cheers.domain.get_location_name

import com.salazar.cheers.data.map.MapRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLocationNameUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        longitude: Double,
        latitude: Double,
        zoom: Double? = null,
    ): Result<List<String>, DataError> = withContext(dispatcher) {
        return@withContext mapRepository.getLocationName(
            longitude = longitude,
            latitude = latitude,
            zoom = zoom,
        )
    }
}