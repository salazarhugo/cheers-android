package com.salazar.cheers.core.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.salazar.cheers.core.ui.ui.ButtonWithLoading


@Composable
fun CoreDialog(
    title: String,
    text: String,
    confirmButton: String,
    dismissButton: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean = false,
) {
    var open by remember { mutableStateOf(true) }

    if (!open)
        return

    AlertDialog(
        onDismissRequest = {
            open = false
        },
        title = {
            Text(
                text = title,
            )
        },
        text = {
            Text(
                text = text,
            )
        },
        dismissButton = {
            if (dismissButton.isNotBlank())
                TextButton(
                    onClick = onDismiss,
                    content = {
                        Text(
                            text = dismissButton,
                        )
                    }
                )
        },
        confirmButton = {
            if (confirmButton.isNotBlank())
                ButtonWithLoading(
                    onClick = onConfirm,
                    text = confirmButton,
                    isLoading = isLoading,
                )
        },
    )
}
