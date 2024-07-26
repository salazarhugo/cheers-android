package com.salazar.cheers.domain.list_chat_members

import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListChatMembersUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(chatID: String) = withContext(dispatcher) {
        return@withContext chatRepository.getRoomMembers(chatID)
    }
}