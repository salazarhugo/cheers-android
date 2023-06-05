package com.salazar.cheers.notes.domain.usecase.list_friend_notes

import com.salazar.cheers.data.note.Note
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFriendNotesUseCase @Inject constructor(
    private val repository: NoteRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Flow<List<Note>> = withContext(dispatcher) {
        return@withContext repository.listFriendNotes()
    }
}