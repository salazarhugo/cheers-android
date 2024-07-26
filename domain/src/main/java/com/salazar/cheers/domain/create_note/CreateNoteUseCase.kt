package com.salazar.cheers.domain.create_note

import com.salazar.cheers.core.model.NoteType
import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    @IODispatcher
    private val dispatcher: CoroutineDispatcher
){
    suspend operator fun invoke(
        text: String? = null,
        type: NoteType = NoteType.NOTHING,
        drinkId: String? = null,
    ) = withContext(dispatcher) {
        return@withContext noteRepository.createNote(
            text = text,
            type = type,
            drinkId = drinkId,
        )
    }
}