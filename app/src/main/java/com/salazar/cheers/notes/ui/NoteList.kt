package com.salazar.cheers.notes.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.notes.domain.models.Note

@Composable
fun NoteList(
    picture: String?,
    notes: List<Note>,
    yourNote: Note?,
    onCreateNoteClick: () -> Unit,
    onNoteClick: (String) -> Unit,
) {
    LazyRow(
        state = rememberLazyListState(),
        modifier = Modifier.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            if (yourNote == null)
                CreateNoteItem(
                    picture = picture,
                    onClick = onCreateNoteClick,
                )
            else
                NoteItem(
                    note = yourNote.copy(name = "Your note"),
                    onClick = onNoteClick,
                )
        }
        items(
            items = notes,
        ) { note ->
            NoteItem(
                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500)),
                note = note,
                onClick = onNoteClick,
            )
        }
    }
}