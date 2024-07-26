package com.salazar.cheers.ui.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun DeletePostDialog(
    onDismiss: () -> Unit,
    viewModel: DialogDeletePostViewModel = hiltViewModel(),
) {
    CoreDialog(
        title = "Delete this post?",
        text = "You can restore this post from Recently deleted in Your activity within 30 days. After that, it will be permanently deleted.",
        dismissButton = stringResource (id = R.string.cancel),
        onDismiss = onDismiss,
        confirmButton = stringResource(id = R.string.delete),
        onConfirm = {
            viewModel.deletePost {
                onDismiss()
            }
        },
    )
}

@Preview
@Composable
private fun DeletePostDialogPreview() {
    CheersPreview {
        DeletePostDialog(
            onDismiss = {},
        )
    }
}