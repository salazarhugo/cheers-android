package com.salazar.cheers.data.note.mapper

import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.model.NoteType


fun cheers.note.v1.Note.toNote(): Note {
    return Note(
        userId = userId,
        text = text,
        name = name,
        username = username,
        picture = picture,
        createTime = created,
        type = type.toNoteType(),
        drinkId = drink.id.toString(),
        drinkName = drink.name,
        drinkIcon = drink.icon,
    )
}

fun cheers.note.v1.NoteType.toNoteType(): NoteType {
    return when(this) {
        cheers.note.v1.NoteType.NOTHING -> NoteType.NOTHING
        cheers.note.v1.NoteType.DRINKING -> NoteType.DRINKING
        cheers.note.v1.NoteType.SEARCHING -> NoteType.SEARCHING
        cheers.note.v1.NoteType.UNRECOGNIZED -> NoteType.NOTHING
    }
}

fun NoteType.toNoteTypePb(): cheers.note.v1.NoteType {
    return when(this) {
        NoteType.NOTHING -> cheers.note.v1.NoteType.NOTHING
        NoteType.DRINKING -> cheers.note.v1.NoteType.DRINKING
        NoteType.SEARCHING -> cheers.note.v1.NoteType.SEARCHING
    }
}
