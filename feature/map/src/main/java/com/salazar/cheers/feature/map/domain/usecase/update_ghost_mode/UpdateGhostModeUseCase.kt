package com.salazar.cheers.feature.map.domain.usecase.update_ghost_mode

import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.data.map.MapRepositoryImpl
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateGhostModeUseCase @Inject constructor(
    private val mapRepository: com.salazar.cheers.data.map.MapRepositoryImpl,
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