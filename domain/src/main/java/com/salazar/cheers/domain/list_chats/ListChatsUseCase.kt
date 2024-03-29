package com.salazar.cheers.domain.list_chats

import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke() = withContext(dispatcher) {
        return@withContext chatRepository.getChannels()
    }
}