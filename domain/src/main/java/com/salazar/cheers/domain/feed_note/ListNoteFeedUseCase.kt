package com.salazar.cheers.domain.feed_note

import com.salazar.cheers.core.model.Note
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListNoteFeedUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val noteRepository: NoteRepository,
) {
    suspend operator fun invoke(): Flow<List<Note>> {
        val userId = getAccountUseCase().first()?.id
            ?: return emptyFlow()
        return noteRepository.listFriendNotes().map {
            it.map { it.copy(isViewer = it.userId == userId) }
        }
    }
}