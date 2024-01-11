package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
internal fun MoreDialog(
    openDialog: Boolean,
    modifier: Modifier = Modifier,
    onCopyUrl: () -> Unit = {},
    onManageFriendship: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onManageFriendship()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Manage Friendship")
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Block")
                }
                TextButton(
                    onClick = {
                        onCopyUrl()
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Copy Profile URL")
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Send Profile as Message")
                }
            }
        },
        confirmButton = {
        },
    )
}

@ComponentPreviews
@Composable
private fun OtherProfileMoreBottomSheetPreview() {
    CheersPreview {
        MoreDialog(
            openDialog = true,
            modifier = Modifier,
        )
    }
}
