package com.salazar.cheers.domain.delete_note

import com.salazar.cheers.data.note.repository.NoteRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val repository: NoteRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        val userId = getAccountUseCase().firstOrNull()?.id ?: return@withContext Result.failure(Exception("not authenticated"))
        return@withContext repository.deleteNote(userID = userId)
    }
}