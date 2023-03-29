package com.salazar.cheers.map.domain.usecase.update_ghost_mode

import com.salazar.cheers.data.datastore.DataStoreRepository
import com.salazar.cheers.di.IODispatcher
import com.salazar.cheers.map.data.repository.MapRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateGhostModeUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val dataStoreRepository: DataStoreRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(ghostMode: Boolean) = withContext(dispatcher) {
        val result = mapRepository.updateGhostMode(ghostMode = ghostMode)
        result.onSuccess {
            dataStoreRepository.updateGhostMode(ghostMode = ghostMode)
        }

        return@withContext
    }
}