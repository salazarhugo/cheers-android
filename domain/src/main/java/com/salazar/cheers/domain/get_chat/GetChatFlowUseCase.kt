package com.salazar.cheers.domain.get_chat

import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.chat.websocket.ChatWebSocketManager
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetChatFlowUseCase @Inject constructor(
    private val webSocketManager: ChatWebSocketManager,
    private val repository: ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        chatID: String,
    ): Flow<ChatChannel> = withContext(dispatcher) {
//        combine(
//            repository.getChannel(channelId = chatID),
//            webSocketManager.websocketState,
//        ) { a, b ->
//            a.copy(
//                isOtherUserPresent = a.isOtherUserPresent,
//                isOtherUserTyping = b.isOtherUserPresent,
//            )
//        }
        return@withContext repository.getChannel(channelId = chatID)
    }
}