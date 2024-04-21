package com.salazar.cheers.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    onProfileSheetUIAction: (ProfileSheetUIAction) -> Unit,
) {
    Column(
        modifier = Modifier.navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.outline)
        )
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

