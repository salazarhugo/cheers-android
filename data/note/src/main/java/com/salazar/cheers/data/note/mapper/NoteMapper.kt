package com.salazar.cheers.data.note.mapper

import com.salazar.cheers.data.note.Note


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