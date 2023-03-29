package com.salazar.cheers.notes.data.mapper

import com.salazar.cheers.notes.domain.models.Note


fun cheers.note.v1.Note.toNote(): Note {
    return Note(
        userId = userId,
        text = text,
        name = name,
        username = username,
        picture = picture,
        createTime = created,
    )
}