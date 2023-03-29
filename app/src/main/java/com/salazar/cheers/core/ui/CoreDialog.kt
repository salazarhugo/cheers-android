package com.salazar.cheers.core.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.salazar.cheers.R
import com.salazar.cheers.core.domain.model.ErrorMessage


@Composable
fun CoreDialog(
    title: String,
    text: String,
    confirmButton: String,
    dismissButton: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
            Button(
                onClick = onConfirm,
                content = {
                    Text(
                        text = confirmButton,
                    )
                }
            )
        },
    )
}
