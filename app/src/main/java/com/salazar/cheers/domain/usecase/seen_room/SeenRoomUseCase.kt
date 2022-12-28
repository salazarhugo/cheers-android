package com.salazar.cheers.domain.usecase.seen_room

import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.di.IODispatcher
import com.salazar.cheers.domain.models.RoomStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SeenRoomUseCase @Inject constructor(
    private val repository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        roomId: String,
    ) = withContext(dispatcher) {
        val room = repository.getChannel(channelId = roomId).first()
        if (room.status != RoomStatus.NEW)
            return@withContext

        repository.setRoomStatus(
            roomId = roomId,
            status = RoomStatus.RECEIVED,
        )
    }
}