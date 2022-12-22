package com.salazar.cheers.domain.usecase.send_message

import cheers.chat.v1.SendMessageResponse
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.internal.RoomStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        roomId: String,
        text: String,
    ): Resource<SendMessageResponse> {
        repository.setRoomStatus(
            roomId = roomId,
            status = RoomStatus.SENT,
        )
        return repository.sendMessage(roomId = roomId, text = text)
    }
}