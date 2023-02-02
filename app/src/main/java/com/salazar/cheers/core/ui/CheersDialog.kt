package com.salazar.cheers.core.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.salazar.cheers.core.domain.model.ErrorMessage


@Composable
fun CheersDialog(
    error: ErrorMessage?,
    openDialog: Boolean,
    onDismiss: () -> Unit,
) {
    if (error == null || !openDialog)
        return

    AlertDialog(
        title = { Text(text = error.title) },
        text = { Text(text = error.text) },
        confirmButton = { },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Dismiss", color = Color.White)
            }
        },
        onDismissRequest = onDismiss,
        textContentColor = Color.White,
        titleContentColor = Color.White,
    )
}
