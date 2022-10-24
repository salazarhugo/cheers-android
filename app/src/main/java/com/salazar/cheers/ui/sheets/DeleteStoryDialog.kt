package com.salazar.cheers.ui.sheets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.CheersAppState

@Composable
fun DeleteStoryDialog(
    appState: CheersAppState,
    navActions: CheersNavigationActions,
    viewModel: DialogDeleteStoryViewModel = hiltViewModel(),
) {
    var open by remember { mutableStateOf(true) }

    val errorMessage = viewModel.errorMessage.collectAsState().value

    if (errorMessage != null) {
        LaunchedEffect(appState.snackBarHostState) {
            appState.showSnackBar(errorMessage)
        }
    }

    if (open)
        AlertDialog(
            onDismissRequest = {
                open = false
            },
            title = {
                Text("Delete this image?")
            },
            text = {
                Text("You can restore this story from Recently deleted in Your activity within 30 days. After that, it will be permanently deleted.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePost()
                    navActions.navigateBack()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    navActions.navigateBack()
                }) {
                    Text("Cancel")
                }
            }
        )
}