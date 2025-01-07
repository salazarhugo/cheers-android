package com.salazar.cheers.domain.update_chat_filter

import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.model.allFilter
import com.salazar.cheers.core.model.emptyFilter
import com.salazar.cheers.data.chat.repository.ChatFilter
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetChatFiltersUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Flow<List<Filter>> = withContext(dispatcher) {
        return@withContext chatRepository.chatFiltersFlow.mapLatest { filter ->
            val allFilter = allFilter
            val groupFilter = emptyFilter.copy(name = "Groups")
            val unrepliedFilter = emptyFilter.copy(name = "Unreplied")
            val filters = listOf(allFilter, groupFilter, unrepliedFilter)

            val selectedFilter = when (filter) {
                ChatFilter.NONE -> allFilter
                ChatFilter.GROUPS -> groupFilter
                ChatFilter.UNREPLIED -> unrepliedFilter
            }
            return@mapLatest filters.map { it.copy(selected = it == selectedFilter) }
        }
    }
}