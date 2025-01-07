package com.salazar.cheers.domain.send_message

import android.net.Uri
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: com.salazar.cheers.data.chat.repository.ChatRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        chatChannelID: String,
        text: String,
        replyTo: String?,
        images: List<Uri>,
    ): Result<Unit, DataError.Network> = withContext(dispatcher) {
        return@withContext repository.enqueueChatMessage(
            chatChannelID = chatChannelID,
            text = text,
            images = images,
            replyTo = replyTo,
        )
    }
}