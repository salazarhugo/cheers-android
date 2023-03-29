package com.salazar.cheers.notes.data.db

import androidx.room.*
import com.salazar.cheers.notes.domain.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<Note>)

    @Query("SELECT * FROM notes ORDER BY createTime DESC")
    fun listNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userID")
    fun getNote(userID: String): Flow<Note>

    @Query("DELETE FROM notes WHERE userId = :userID")
    suspend fun deleteNote(userID: String)

    @Query("DELETE FROM notes")
    suspend fun clear()
}