package com.salazar.cheers.friendship.ui.manage_friendship

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.navigation.CheersNavigationActions

@Composable
fun RemoveFriendDialog(
    navActions: CheersNavigationActions,
    viewModel: ManageFriendshipViewModel = hiltViewModel(),
) {
    CoreDialog(
        title = "Remove friend?",
        text = "Are you sure you want to remove this user as a friend?",
        dismissButton = stringResource(id = R.string.cancel),
        onDismiss = {
            navActions.navigateBack()
        },
        confirmButton = stringResource(id = R.string.remove),
        onConfirm = {
            viewModel.removeFriend {
                navActions.navigateBack()
            }
        },
    )
}