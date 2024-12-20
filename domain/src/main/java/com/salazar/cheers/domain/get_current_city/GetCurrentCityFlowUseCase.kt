package com.salazar.cheers.domain.get_current_city

import android.location.Geocoder
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.get_last_known_location.GetLastKnownLocationUseCase
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentCityFlowUseCase @Inject constructor(
    private val geocoder: Geocoder,
    private val dataStoreRepository: DataStoreRepository,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Flow<String> {
        return withContext(dispatcher) {
            dataStoreRepository.getLocationEnabled()
                .flatMapConcat { shouldUsePhoneLocation ->
                    if (!shouldUsePhoneLocation) {
                        return@flatMapConcat dataStoreRepository.getCityFlow()
                    }

                    val location = getLastKnownLocationUseCase() ?: return@flatMapConcat flowOf("")

                    try {
                        val city = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            ?.firstOrNull()?.locality.orEmpty()

                        if (city.isNotBlank()) {
                            dataStoreRepository.updateCity(city)
                        }

                        return@flatMapConcat flowOf(city)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@flatMapConcat flowOf("")
                    }

                }
        }
    }
}