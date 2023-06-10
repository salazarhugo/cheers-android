package com.salazar.cheers.auth.ui.components.delete_account


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun DeleteAccountDialog(
    navActions: CheersNavigationActions,
    viewModel: DeleteAccountViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val text = uiState.errorMessage.ifBlank {
        "Your account will be permanently deleted."
    }
    if (uiState.errorMessage.isNotBlank())
        CoreDialog(
            title = "Failed to delete account",
            text = text,
            dismissButton = stringResource(id = R.string.dismiss),
            onDismiss = {
                navActions.navigateBack()
            },
            confirmButton = "",
            onConfirm = {
                viewModel.deleteAccount { success ->
                    if (success) {
                        navActions.navigateToSignIn()
                    }
                }
            },
        )
    else
        CoreDialog(
            title = "Delete account?",
            text = text,
            dismissButton = stringResource(id = R.string.cancel),
            onDismiss = {
                navActions.navigateBack()
            },
            confirmButton = stringResource(id = R.string.delete),
            onConfirm = {
                viewModel.deleteAccount { success ->
                    if (success) {
                        navActions.navigateToSignIn()
                    }
                }
            },
        )
}
