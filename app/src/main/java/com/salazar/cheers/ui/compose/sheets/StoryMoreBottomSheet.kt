package com.salazar.cheers.ui.compose.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.SheetItem

sealed class StorySheetUIAction {
    data object OnNfcClick : StorySheetUIAction()
    data object OnCopyStoryClick : StorySheetUIAction()
    data object OnSettingsClick : StorySheetUIAction()
    data object OnDeleteClick : StorySheetUIAction()
    data object OnAddSnapchatFriends : StorySheetUIAction()
    data object OnPostHistoryClick : StorySheetUIAction()
}

@Composable
fun StoryMoreBottomSheet(
    onStorySheetUIAction: (StorySheetUIAction) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.outline)
        )
        SheetItem(
            text = "Delete",
            icon = Icons.Outlined.Delete,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnDeleteClick) },
        )
        SheetItem(
            text = "Save video",
            icon = Icons.Outlined.Archive,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnAddSnapchatFriends) }
        )
        SheetItem(
            text = "Send to...",
            icon = Icons.Outlined.Archive,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnPostHistoryClick) }
        )
        SheetItem(
            text = "Share as post...",
            icon = Icons.Outlined.Contactless,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnNfcClick) }
        )
        SheetItem(text = "Copy link", icon = Icons.Outlined.QrCode)
        SheetItem(text = "Saved", icon = Icons.Outlined.BookmarkBorder)
        SheetItem(
            text = "Share to...",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnCopyStoryClick) }
        )
        SheetItem(
            text = "Story settings",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnCopyStoryClick) }
        )
    }
}