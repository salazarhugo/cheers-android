package com.salazar.cheers.domain.update_chat_filter

import com.salazar.cheers.data.chat.repository.ChatFilter
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateChatFilterUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        chatFilter: ChatFilter,
    ): Unit = withContext(dispatcher) {
        val selectedChatFilter = chatRepository.chatFiltersFlow.value

        val newChatFilter = if (chatFilter == selectedChatFilter) {
            ChatFilter.NONE
        } else {
            chatFilter
        }

        chatRepository.updateChatFilter(chatFilter = newChatFilter)
    }
}