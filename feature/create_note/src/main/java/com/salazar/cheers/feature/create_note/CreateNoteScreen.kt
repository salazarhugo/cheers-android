package com.salazar.cheers.feature.create_note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .imePadding()
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
