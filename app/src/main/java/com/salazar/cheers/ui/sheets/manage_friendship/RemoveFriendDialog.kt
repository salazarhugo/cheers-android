package com.salazar.cheers.ui.sheets.manage_friendship

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.sheets.DialogDeletePostViewModel

@Composable
fun RemoveFriendDialog(
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
                Text(
                    text = "Are you sure you want to remove this user as a friend?"
                )
            },
            text = {
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePost {
                        navActions.navigateBack()
                    }
                }) {
                    Text("Remove")
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