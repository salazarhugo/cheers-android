package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CoreDialog
import com.salazar.cheers.core.ui.SheetItem


@Composable
fun PartyManageSheet(
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onCopyLink: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        CoreDialog(
            title = "Delete this party?",
            text = "It will be permanently deleted.",
            dismissButton = stringResource(id = com.salazar.cheers.core.ui.R.string.cancel),
            onDismiss = onDismiss,
            confirmButton = stringResource(id = com.salazar.cheers.core.ui.R.string.delete),
            onConfirm = onDeleteClick,
        )
    }
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SheetItem(
                text = "Edit",
                icon = Icons.Outlined.Edit,
                onClick = onEditClick,
            )
            SheetItem(
                text = "Delete",
                icon = Icons.Outlined.Delete,
                onClick = {
                    showDialog = true
                },
            )
            SheetItem(
                text = "Copy link",
                icon = Icons.Outlined.Link,
                onClick = onCopyLink,
            )
            SheetItem(
                text = "Save",
                icon = Icons.Outlined.Save,
                onClick = {}
            )
        }
    }
}

