package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.model.NoteType

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val userId: String,
    val text: String = "",
    val picture: String = "",
    val username: String = "",
    val name: String = "",
    val createTime: Long = 0,
    val isViewer: Boolean = false,
    val type: NoteType = NoteType.NOTHING,
    val drinkId: String = String(),
    val drinkIcon: String = String(),
    val drinkName: String = String(),
)

fun NoteEntity.asExternalModel() = Note(
    userId = userId,
    text = text,
    picture = picture,
    username = username,
    name = name,
    createTime = createTime,
    drinkId = drinkId,
    drinkName = drinkName,
    type = type,
    drinkIcon = drinkIcon,
    isViewer = isViewer,
)

fun Note.asEntity(): NoteEntity =
    NoteEntity(
        userId = userId,
        text = text,
        picture = picture,
        username = username,
        name = name,
        createTime = createTime,
        drinkId = drinkId,
        drinkName = drinkName,
        type = type,
        drinkIcon = drinkIcon,
        isViewer = isViewer,
    )

fun List<NoteEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Note>.asEntity() = this.map { it.asEntity() }
