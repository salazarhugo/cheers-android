package com.salazar.cheers.feature.chat.data

import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatType
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.common.di.IODispatcher
import com.salazar.common.util.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetChatChannelUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepositoryImpl: UserRepositoryImpl,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        userID: String,
    ): Flow<ChatChannel> = withContext(dispatcher) {
        return@withContext flow {
            val user = userRepositoryImpl.getUserItem(userID).first()
            val localChat = chatRepository.getChatWithUser(userID)
            val tempChatChannel = ChatChannel(
                id = "temp",
                name = user.username,
                picture = user.picture,
                verified = user.verified,
                type = ChatType.DIRECT,
            )
            emit(localChat ?: tempChatChannel)
            val result = chatRepository.getOrCreateDirectChat(userID)
            when(result) {
                is Result.Error -> {}
                is Result.Success -> {
                    emitAll(chatRepository.getChannel(result.data.id))
                }
            }
        }
    }
}