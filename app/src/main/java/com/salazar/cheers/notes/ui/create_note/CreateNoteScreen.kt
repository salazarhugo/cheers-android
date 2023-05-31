package com.salazar.cheers.notes.ui.create_note

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.ui.main.add.ShareButton
import com.salazar.cheers.feature.chat.ui.chats.SearchBar
import com.salazar.cheers.core.ui.theme.Roboto

@Composable
fun CreateNoteScreen(
    uiState: CreateNoteUiState,
    onCreateNoteUIAction: (CreateNoteUIAction) -> Unit,
) {
    val text = uiState.text
    Scaffold(
        topBar = {
            TopAppBar(
                onDismiss = {
                    onCreateNoteUIAction(CreateNoteUIAction.OnBackPressed)
                },
            )
        },
        bottomBar = {
            ShareButton(
                modifier = Modifier.navigationBarsPadding(),
                text = stringResource(id = R.string.share),
                isLoading = uiState.isLoading,
                onClick = {
                    onCreateNoteUIAction(CreateNoteUIAction.OnCreateNote)
                },
                enabled = text.length in 1..60,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp),
        ) {
            Text(
                text = "People won't be notified when you leave a note. They can see your note for 24 hours.",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                searchInput = text,
                onSearchInputChanged = { text ->
                    onCreateNoteUIAction(CreateNoteUIAction.OnTextChange(text))
                },
                placeholder = {
                    Text(
                        text = "Share what's on your mind..."
                    )
                },
                autoFocus = true,
            )
            Text(
                text = "${text.length}/60",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }

//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }
}

@Composable
fun TopAppBar(
    onDismiss: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.new_note),
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto,
            )
        },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, null)
            }
        },
    )
}
