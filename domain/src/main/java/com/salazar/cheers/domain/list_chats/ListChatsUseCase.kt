package com.salazar.cheers.domain.list_chats

import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.ChatType
import com.salazar.cheers.data.chat.repository.ChatFilter
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Flow<List<ChatChannel>> = withContext(dispatcher) {

        val chatChannelsFlow = chatRepository.getChannels()
        val filtersFlow = chatRepository.chatFiltersFlow

        return@withContext combine(chatChannelsFlow, filtersFlow) { chats, filters ->
            filterSortChats(
                chats = chats,
                chatFilter = filters,
            )
        }
    }
}

private fun filterSortChats(
    chats: List<ChatChannel>,
    chatFilter: ChatFilter,
): List<ChatChannel> {

    // filter the chats
    return when (chatFilter) {
        // We shouldn't get any other values
        ChatFilter.NONE -> chats
        ChatFilter.GROUPS -> chats.filter { it.type == ChatType.GROUP }
        ChatFilter.UNREPLIED -> chats.filter { it.status in listOf(ChatStatus.RECEIVED, ChatStatus.NEW) }
    }
}