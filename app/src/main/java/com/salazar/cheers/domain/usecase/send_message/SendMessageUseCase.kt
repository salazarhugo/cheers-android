package com.salazar.cheers.domain.usecase.send_message

import cheers.chat.v1.SendMessageResponse
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.di.IODispatcher
import com.salazar.cheers.internal.RoomStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        roomId: String,
        text: String,
    ): Resource<SendMessageResponse> = withContext(dispatcher) {
        repository.setRoomStatus(
            roomId = roomId,
            status = RoomStatus.SENT,
        )

        return@withContext repository.sendMessage(roomId = roomId, text = text)
    }
}