package com.salazar.cheers.feature.map.domain.usecase.list_friend_location

import com.salazar.cheers.data.map.MapRepositoryImpl
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFriendLocationUseCase @Inject constructor(
    private val mapRepository: com.salazar.cheers.data.map.MapRepositoryImpl,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        return@withContext mapRepository.listFriendLocation()
    }
}