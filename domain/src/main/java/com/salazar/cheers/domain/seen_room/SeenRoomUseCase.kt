package com.salazar.cheers.domain.seen_room

import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SeenRoomUseCase @Inject constructor(
    private val repository: ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        roomId: String,
    ) = withContext(dispatcher) {
        val room = repository.getChannel(channelId = roomId).first()
        if (room.status != ChatStatus.NEW)
            return@withContext

        repository.setRoomStatus(
            roomId = roomId,
            status = ChatStatus.RECEIVED,
        )
    }
}