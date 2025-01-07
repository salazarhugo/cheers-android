package com.salazar.cheers.feature.chat.data

import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetChatChannelUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        userID: String,
    ): Flow<ChatChannel> = withContext(dispatcher) {
        return@withContext flow {
            val localChat = chatRepository.getChatWithUser(userID)
            if (localChat != null) {
                emit(localChat)
            }
            val result = chatRepository.getOrCreateDirectChat(userID)
            when (result) {
                is Result.Error -> {}
                is Result.Success -> {
                    emitAll(chatRepository.getChannel(result.data.id))
                }
            }
        }
    }
}