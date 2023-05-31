package com.salazar.cheers.ui.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.core.share.ui.CheersNavigationActions

@Composable
fun DeletePostDialog(
    navActions: CheersNavigationActions,
    viewModel: DialogDeletePostViewModel = hiltViewModel(),
) {
    CoreDialog(
        title = "Delete this post?",
        text = "You can restore this post from Recently deleted in Your activity within 30 days. After that, it will be permanently deleted.",
        dismissButton = stringResource (id = R.string.cancel),
        onDismiss = {
            navActions.navigateBack()
        },
        confirmButton = stringResource(id = R.string.delete),
        onConfirm = {
            viewModel.deletePost {
                navActions.navigateBack()
            }
        },
    )
}