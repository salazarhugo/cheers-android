package com.salazar.cheers.feature.chat.domain.usecase.pin_room

import com.salazar.cheers.feature.chat.data.repository.ChatRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PinRoomUseCase @Inject constructor(
    private val repository: ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(roomId: String) = withContext(dispatcher) {
        return@withContext repository.pinRoom(roomId = roomId)
    }
}