package com.salazar.cheers.domain.usecase.get_unread_chat_counter

import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadChatCounterUseCase @Inject constructor(
    private val repository: ChatRepository,
) {
    operator fun invoke(): Flow<Int> {
        return repository.getUnreadChatCount()
    }
}