package com.salazar.cheers.comment.ui.delete

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.navigation.CheersNavigationActions

@Composable
fun DeleteCommentDialog(
    navActions: CheersNavigationActions,
    viewModel: DeleteCommentViewModel = hiltViewModel(),
) {
    CoreDialog(
        title = "Delete comment?",
        text = "Your comment will be permanently deleted.",
        dismissButton = stringResource (id = R.string.cancel),
        onDismiss = {
            navActions.navigateBack()
        },
        confirmButton = stringResource(id = R.string.delete),
        onConfirm = {
            viewModel.deleteComment {
                navActions.navigateBack()
            }
        },
    )
}