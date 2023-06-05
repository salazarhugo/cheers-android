package com.salazar.cheers.data.note.db

import androidx.room.*
import com.salazar.cheers.data.note.Note
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<Note>)

    @Query("SELECT * FROM notes WHERE createTime > :yesterday ORDER BY createTime DESC")
    fun listNotes(
        yesterday: Long = (Date().time / 1000) - 60 * 60 * 24
    ): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userID")
    fun getNote(userID: String): Flow<Note>

    @Query("DELETE FROM notes WHERE userId = :userID")
    suspend fun deleteNote(userID: String)

    @Query("DELETE FROM notes")
    suspend fun clear()
}