package com.salazar.cheers.feature.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Note

@Composable
fun NoteList(
    picture: String?,
    notes: List<Note>,
    modifier: Modifier = Modifier,
    onCreateNoteClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val viewerNote = remember(notes) {
        notes.firstOrNull { it.isViewer }
    }

    val friendNotes = remember(notes) {
        notes.filterNot { it.isViewer }
    }

    LazyRow(
        state = rememberLazyListState(),
        modifier = modifier.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            if (viewerNote == null) {
                CreateNoteItem(
                    picture = picture,
                    onClick = onCreateNoteClick,
                )
            } else {
                NoteItem(
                    note = viewerNote.copy(name = "Me"),
                    onClick = onNoteClick,
                    onUserClick = onUserClick,
                )
            }
        }
        items(
            items = friendNotes,
        ) { note ->
            NoteItem(
                note = note,
                modifier = Modifier.animateItem(
                    placementSpec = tween(durationMillis = 500)
                ),
                onClick = onNoteClick,
                onUserClick = onUserClick,
            )
        }
    }
}