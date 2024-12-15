package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.SheetItem


sealed class ProfileSheetUIAction {
    data object OnNfcClick : ProfileSheetUIAction()
    data object OnCopyProfileClick : ProfileSheetUIAction()
    data object OnSettingsClick : ProfileSheetUIAction()
    data object OnAddSnapchatFriends : ProfileSheetUIAction()
    data object OnPostHistoryClick : ProfileSheetUIAction()
    data object OnQrCodeClick : ProfileSheetUIAction()
}

@Composable
fun ProfileMoreBottomSheet(
    sheetState: SheetState,
    onProfileSheetUIAction: (ProfileSheetUIAction) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SheetItem(
                text = "Settings",
                icon = Icons.Outlined.Settings,
                onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnSettingsClick) },
            )
            SheetItem(
                text = "Add Snapchat Friends",
                icon = Icons.Outlined.Archive,
                onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnAddSnapchatFriends) },
            )
            SheetItem(
                text = "Post History",
                icon = Icons.Outlined.Archive,
                onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnPostHistoryClick) },
            )
            SheetItem(
                text = "Nfc",
                icon = Icons.Outlined.Contactless,
                onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnNfcClick) },
            )
            SheetItem(
                text = "QR code",
                icon = Icons.Outlined.QrCode,
                onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnQrCodeClick) },
            )
            SheetItem(
                text = "Saved",
                icon = Icons.Outlined.BookmarkBorder,
            )
            SheetItem(
                text = "Copy Profile URL",
                icon = Icons.Outlined.ContentCopy,
                onClick = { onProfileSheetUIAction(ProfileSheetUIAction.OnCopyProfileClick) }
            )
        }
    }
}

