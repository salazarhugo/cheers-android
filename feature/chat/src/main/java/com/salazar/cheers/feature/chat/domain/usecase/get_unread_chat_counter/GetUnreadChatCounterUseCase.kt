package com.salazar.cheers.feature.chat.domain.usecase.get_unread_chat_counter

import com.salazar.cheers.feature.chat.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadChatCounterUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    operator fun invoke(): Flow<Int> {
        return repository.getUnreadChatCount()
    }
}