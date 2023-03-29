package com.salazar.cheers.map.domain.usecase.list_friend_location

import com.salazar.cheers.di.IODispatcher
import com.salazar.cheers.map.data.repository.MapRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFriendLocationUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        return@withContext mapRepository.listFriendLocation()
    }
}