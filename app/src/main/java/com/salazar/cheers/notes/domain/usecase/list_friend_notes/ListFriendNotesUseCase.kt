package com.salazar.cheers.notes.domain.usecase.list_friend_notes

import com.salazar.cheers.core.di.IODispatcher
import com.salazar.cheers.notes.data.repository.NoteRepository
import com.salazar.cheers.notes.domain.models.Note
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