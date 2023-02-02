package com.salazar.cheers.ui.sheets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions

@Composable
fun DeletePostDialog(
    navActions: CheersNavigationActions,
    viewModel: DialogDeletePostViewModel = hiltViewModel(),
) {
    var open by remember { mutableStateOf(true) }

    if (open)
        AlertDialog(
            onDismissRequest = {
                open = false
            },
            title = {
                Text("Delete this post?")
            },
            text = {
                Text("You can restore this post from Recently deleted in Your activity within 30 days. After that, it will be permanently deleted.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePost {
                        navActions.navigateBack()
                    }
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