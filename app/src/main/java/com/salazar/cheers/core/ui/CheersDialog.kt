package com.salazar.cheers.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.salazar.cheers.R
import com.salazar.cheers.core.domain.model.ErrorMessage


@Composable
fun CheersDialog(
    error: ErrorMessage?,
    openDialog: Boolean,
    onDismiss: () -> Unit,
) {
    if (error == null || !openDialog)
        return

    CoreDialog(
        title = error.title,
        text = error.text,
        dismissButton = stringResource(id = R.string.dismiss),
        confirmButton = "",
        onDismiss = onDismiss,
        onConfirm = {},
    )
}
