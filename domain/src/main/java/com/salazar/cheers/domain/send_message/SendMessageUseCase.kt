package com.salazar.cheers.domain.send_message

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.MessageType
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: com.salazar.cheers.data.chat.repository.ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        roomId: String,
        text: String,
    ): Result<ChatMessage, DataError.Network> = withContext(dispatcher) {
        val user = UserItem()
        val id = UUID.randomUUID().toString()

        val localChatMessage = ChatMessage(
            id = id,
            senderId = user.id,
            senderName = user.name,
            senderUsername = user.username,
            senderProfilePictureUrl = user.picture ?: "",
            roomId = roomId,
            text = text,
            type = MessageType.TEXT,
            status = ChatMessageStatus.SCHEDULED,
            photoUrl = "",
            seenBy = emptyList(),
            likedBy = emptyList(),
            createTime = Date().time,
            isSender = true,
            hasLiked = false,
        )

        return@withContext repository.sendMessage(
            roomId = roomId,
            message = localChatMessage,
        )
    }
}