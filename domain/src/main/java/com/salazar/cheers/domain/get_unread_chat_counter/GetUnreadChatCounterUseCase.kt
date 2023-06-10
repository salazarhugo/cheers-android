package com.salazar.cheers.domain.get_unread_chat_counter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUnreadChatCounterUseCase @Inject constructor(
//    private val repository: ChatRepository,
) {
    operator fun invoke(): Flow<Int> {
        return flowOf(0)//repository.getUnreadChatCount()
    }
}