package com.salazar.cheers.domain.usecase.seen_room

import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.internal.RoomStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SeenRoomUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(roomId: String) {
        val room = repository.getChannel(channelId = roomId).first()
        if (room.status != RoomStatus.NEW)
            return

        repository.setRoomStatus(
            roomId = roomId,
            status = RoomStatus.RECEIVED,
        )
    }
}