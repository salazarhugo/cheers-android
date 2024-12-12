package com.salazar.cheers.ui.compose.bottombar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.emptyUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.login_message.LoginMessage
import com.salazar.cheers.data.account.Account


@Composable
fun AccountBottomSheet(
    viewModel: AccountBottomSheetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val account = uiState.account

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
    ) {
        if (account != null) {
            AccountItem(
                account = account,
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    viewModel.onSignOut {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = "Sign out"
                )
            }
        } else {
            LoginMessage(
                onSignInClick = {},
                onRegisterClick = {},
            )
        }
    }
}

@Composable
fun AccountItem(
    account: Account,
    modifier: Modifier = Modifier,
) {
    UserItem(
        modifier = modifier,
        userItem = emptyUserItem.copy(
            name = account.name,
            picture = account.picture,
            username = account.username,
            verified = account.verified,
        ),
    )
}


@ScreenPreviews
@Composable
private fun AccountBottomSheetPreview() {
    CheersPreview {
        AccountBottomSheet(
            modifier = Modifier,
        )
    }
}