package com.salazar.cheers.domain.usecase.send_message

import cheers.chat.v1.SendMessageResponse
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.di.IODispatcher
import com.salazar.cheers.domain.models.ChatMessage
import com.salazar.cheers.domain.models.ChatMessageStatus
import com.salazar.cheers.domain.models.MessageType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        roomId: String,
        text: String,
    ): Resource<SendMessageResponse> = withContext(dispatcher) {
        val user = userRepository.getCurrentUser()
        val id = UUID.randomUUID().toString()

        val localChatMessage = ChatMessage(
            id = id,
            senderId = user.id,
            senderName= user.name,
            senderUsername = user.username,
            senderProfilePictureUrl = user.picture ?: "",
            roomId = roomId,
            text = text,
            type = MessageType.TEXT,
            status = ChatMessageStatus.SCHEDULED,
            photoUrl = "",
            seenBy = emptyList(),
            likedBy = emptyList(),
            createTime = Date().time / 1000,
        )

        return@withContext repository.sendMessage(
            roomId = roomId,
            message = localChatMessage,
        )
    }
}