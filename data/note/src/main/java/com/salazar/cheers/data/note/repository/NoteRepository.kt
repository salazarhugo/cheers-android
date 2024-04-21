package com.salazar.cheers.data.note.repository

import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.model.NoteType
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Note data layer.
 */
interface NoteRepository {

    /**
     * Create a new note
     */
    suspend fun createNote(
        text: String?,
        type: NoteType,
        drinkId: String?,
    ): Result<Note>

    /**
     * Get the note of a specific user
     */
    suspend fun getNote(userID: String): Flow<Note>

    /**
     * Get the note of the current user
     */
    suspend fun getYourNote(): Flow<Note>

    /**
     * List friend notes.
     */
    fun listFriendNotes(): Flow<List<Note>>

    /**
     * Refresh friend notes from remote
     */
    suspend fun refreshFriendNotes(): Result<Unit>

    /**
     * Delete a note
     */
    suspend fun deleteNote(userID: String): Result<Unit>
}