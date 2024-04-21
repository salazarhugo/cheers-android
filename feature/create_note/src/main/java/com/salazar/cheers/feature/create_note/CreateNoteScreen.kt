package com.salazar.cheers.feature.create_note

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.ShareButton
import com.salazar.cheers.core.ui.theme.Roboto
import kotlin.math.max

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
                text = "Share",
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
            Spacer(modifier = Modifier.height(16.dp))

            CheersSearchBar(
                searchInput = text,
                onSearchInputChanged = { text ->
                    onCreateNoteUIAction(CreateNoteUIAction.OnTextChange(text))
                },
                placeholder = {
                    Text(
                        text = "What're you up to?"
                    )
                },
                autoFocus = true,
            )
            Text(
                text = "${max(0, 60 - text.length)}",
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
                text = "New Note",
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
