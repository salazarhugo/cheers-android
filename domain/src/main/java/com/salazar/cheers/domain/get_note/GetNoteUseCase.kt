package com.salazar.cheers.domain.get_note

import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.model.UserID
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(
        userID: UserID,
    ): Flow<Note> = withContext(dispatcher) {
        return@withContext repository.getNote(userID = userID)
    }
}