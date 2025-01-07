package com.salazar.cheers.domain.send_message

import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val repository: ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        chatID: String,
        chatMessageID: String,
    ): Result<Unit, DataError.Network> = withContext(dispatcher) {
        return@withContext repository.deleteChatMessage(
            chatID = chatID,
            chatMessageID = chatMessageID,
        )
    }
}