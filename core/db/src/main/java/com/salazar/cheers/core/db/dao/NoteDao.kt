package com.salazar.cheers.core.db.dao

import androidx.room.*
import com.salazar.cheers.core.db.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<NoteEntity>)

    @Query("SELECT * FROM notes ORDER BY createTime DESC")
    fun listNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE userId = :userID")
    fun getNote(userID: String): Flow<NoteEntity?>

    @Query("DELETE FROM notes WHERE userId = :userID")
    suspend fun deleteNote(userID: String)

    @Query("DELETE FROM notes")
    suspend fun clear()
}