package com.salazar.cheers.chat.domain.usecase.pin_room

import com.salazar.cheers.chat.data.repository.ChatRepository
import com.salazar.cheers.core.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PinRoomUseCase @Inject constructor(
    private val repository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(roomId: String) = withContext(dispatcher) {
        return@withContext repository.pinRoom(roomId = roomId)
    }
}