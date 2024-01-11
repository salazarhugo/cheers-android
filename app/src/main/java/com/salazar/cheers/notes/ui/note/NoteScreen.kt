package com.salazar.cheers.notes.ui.note

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.ui.UserProfilePicture

@Composable
fun NoteScreen(
    uiState: NoteUiState,
    onNoteUIAction: (NoteUIAction) -> Unit,
) {
    val color = MaterialTheme.colorScheme.onBackground
    val note = uiState.note ?: return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarComponent(
                avatar = note.picture,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                val annotatedString = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle().copy(fontSize = 13.sp)) {
                        append(note.name)
                    }
                    withStyle(MaterialTheme.typography.labelMedium.toSpanStyle()) {
                        append(
                            "\" shared a note.\" " + com.salazar.cheers.core.util.relativeTimeFormatter(
                                epoch = note.createTime
                            )
                        )
                    }
                }

                Text(
                    text = annotatedString,
                    color = color,
                )
                Text(
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    color = color,
                )
            }
        }
    }
    val uid = FirebaseAuth.getInstance().currentUser?.uid!!
    if (note.userId == uid) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FilledTonalButton(
                onClick = {
                    onNoteUIAction(NoteUIAction.OnCreateNewNoteClick)
                },
            ) {
                Text(
                    text = "Create new note",
                )
            }
            TextButton(
                onClick = {
                    onNoteUIAction(NoteUIAction.OnDeleteNoteClick)
                },
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                )
            }
        }
    }
}
