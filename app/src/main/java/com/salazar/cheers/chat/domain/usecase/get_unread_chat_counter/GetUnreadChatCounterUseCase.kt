package com.salazar.cheers.chat.domain.usecase.get_unread_chat_counter

import com.salazar.cheers.chat.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadChatCounterUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    operator fun invoke(): Flow<Int> {
        return repository.getUnreadChatCount()
    }
}