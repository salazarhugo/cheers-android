package com.salazar.cheers.data.note.repository

import cheers.note.v1.CreateNoteRequest
import cheers.note.v1.DeleteNoteRequest
import cheers.note.v1.ListFriendNoteRequest
import cheers.note.v1.NoteServiceGrpcKt
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.note.Note
import com.salazar.cheers.data.note.NoteType
import com.salazar.cheers.data.note.db.NoteDao
import com.salazar.cheers.data.note.mapper.toNote
import com.salazar.cheers.data.note.mapper.toNoteTypePb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    private val service: NoteServiceGrpcKt.NoteServiceCoroutineStub,
): NoteRepository {
    override suspend fun createNote(
        text: String?,
        type: NoteType,
        drinkId: String?,
    ): Result<Note> {
        return try {
            var request = CreateNoteRequest.newBuilder()
                .setType(type.toNoteTypePb())

            if (text != null) {
                request.setText(text)
            }
            if (drinkId != null) {
                request.setDrinkId(drinkId)
            }

            val response = service.createNote(request = request.build())
            val note = response.note.toNote()
            dao.insert(note)
            Result.success(note)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getNote(userID: String): Flow<Note> {
        return dao.getNote(userID = userID)
    }

    override suspend fun getYourNote(): Flow<Note> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return getNote(userID = uid)
    }

    override fun listFriendNotes(): Flow<List<Note>>  {
        return dao.listNotes()
    }

    override suspend fun refreshFriendNotes(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = ListFriendNoteRequest.newBuilder().build()
            val response = service.listFriendNote(request = request)
            val notes = response.itemsList.map {
                it.toNote()
            }
            dao.clear()
            dao.insert(notes)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(
        userID: String,
    ): Result<Unit> {
        return try {
            val request = DeleteNoteRequest.newBuilder().build()
            service.deleteNote(request = request)
            dao.deleteNote(userID = userID)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}